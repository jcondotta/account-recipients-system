package com.jcondotta.account_recipients.delete_recipient.controller;

import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import com.jcondotta.account_recipients.application.usecase.shared.value_objects.IdempotencyKey;
import com.jcondotta.account_recipients.common.container.LocalStackTestContainer;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.account_recipients.infrastructure.adapters.output.messaging.EventMetadata;
import com.jcondotta.account_recipients.infrastructure.adapters.output.messaging.RecipientCreatedMessage;
import com.jcondotta.account_recipients.infrastructure.adapters.output.messaging.RecipientDeletedMessage;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import com.jcondotta.account_recipients.infrastructure.config.RecipientsDeletedTestListener;
import com.jcondotta.account_recipients.infrastructure.interfaces.rest.exception_handler.ProblemTypes;
import com.jcondotta.account_recipients.infrastructure.interfaces.rest.headers.HttpHeadersCustom;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE;
import static com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TITLE;
import static com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException.ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE;
import static com.jcondotta.account_recipients.domain.recipient.exceptions.AccountRecipientNotFoundException.ACCOUNT_RECIPIENT_NOT_FOUND_TITLE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class DeleteAccountRecipientControllerImplIT {

  private static final Locale DEFAULT_LOCALE = Locale.US;

  @Autowired
  private DynamoDbTable<AccountRecipientEntity> dynamoDbTable;

  @Autowired
  private AccountRecipientEntityMapper entityMapper;

  @Autowired
  private AccountRecipientURIProperties uriProperties;

  @Autowired
  private Clock fixedClock;

  @Autowired
  private MessageResolverPort messageResolverPort;

  @Autowired
  private RecipientsDeletedTestListener listener;

  private RecipientId recipientId;
  private BankAccountId bankAccountId;
  private RecipientName recipientName;
  private Iban iban;

  private ZonedDateTime fixedZonedDateTime;

  private RequestSpecification requestSpecification;
  private IdempotencyKey idempotencyKey;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    recipientId = RecipientId.newId();
    bankAccountId = BankAccountId.of(UUID.randomUUID());
    recipientName = RecipientName.of(AccountRecipientFixtures.JEFFERSON.getRecipientName());
    iban = Iban.of(AccountRecipientFixtures.JEFFERSON.getRecipientIban());
    fixedZonedDateTime = ZonedDateTime.now(fixedClock);

    idempotencyKey = IdempotencyKey.newKey();
    requestSpecification = buildRequestSpecificationWithIdempotencyKey(idempotencyKey);
    listener.clear();
  }

  @Test
  void shouldReturn204NoContent_whenAccountRecipientIsFound() {
    stubFor(
        get(urlPathEqualTo("/api/v1/bank-accounts/" + bankAccountId))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("bank-accounts/bank-account-active.json")
                    .withTransformers("response-template")));

    var accountRecipient = AccountRecipient.restore(recipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
    var accountRecipientEntity = seed(accountRecipient);

    given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", accountRecipient.getBankAccountId().value())
        .pathParam("recipient-id", accountRecipient.getRecipientId().value())
    .when()
        .delete()
    .then()
        .statusCode(HttpStatus.NO_CONTENT.value());

    var key = buildRecipientKey(accountRecipientEntity);
    assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
        .as("The account recipient entity should be deleted from the database")
        .isNull();

    try {
      EventEnvelope<RecipientDeletedMessage> eventEnvelope = listener.awaitEvent(
          Duration.ofSeconds(2), RecipientDeletedMessage.class,
          envelope ->
              envelope.metadata().idempotencyKey().equals(idempotencyKey.value())
      );
      assertThat(eventEnvelope)
          .satisfies(envelope -> {
            EventMetadata eventMetadata = eventEnvelope.metadata();
            assertAll(
                () -> assertThat(eventMetadata.idempotencyKey()).isEqualTo(idempotencyKey.value()),
                () -> assertThat(eventMetadata.publishedAt()).isNotNull()
            );

            RecipientDeletedMessage message = envelope.payload();
            assertAll(
                () -> assertThat(message.recipientId()).isNotBlank(),
                () -> assertThat(message.bankAccountId()).isEqualTo(bankAccountId.toString()),
                () -> assertThat(message.occurredAt()).isNotNull()
            );
          });
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var accountRecipient = AccountRecipient.restore(recipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
    var accountRecipientEntity = seed(accountRecipient);

    var nonExistentBankAccountId = UUID.randomUUID();
    var problemDetail =
        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", nonExistentBankAccountId)
            .pathParam("recipient-id", accountRecipient.getRecipientId().value())
            .when()
            .delete()
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .body()
            .as(ProblemDetail.class);

            var expectedMessageError = resolveMessage(BANK_ACCOUNT_NOT_FOUND_TEMPLATE, DEFAULT_LOCALE, nonExistentBankAccountId, recipientId.value());
            assertAll(
                () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
                () -> assertThat(problemDetail.getTitle()).hasToString(BANK_ACCOUNT_NOT_FOUND_TITLE),
                () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
                () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientURI(nonExistentBankAccountId, recipientId.value())));

    var key = buildRecipientKey(accountRecipientEntity);
    assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
        .as(
            "Recipient must remain because bank account %s does not exist",
            nonExistentBankAccountId)
        .isNotNull();
  }

  @Test
  void shouldReturn404NotFound_whenAccountRecipientDoesNotExist() {
    stubFor(
        get(urlPathEqualTo("/api/v1/bank-accounts/" + bankAccountId))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("bank-accounts/bank-account-active.json")
                    .withTransformers("response-template")));

    var accountRecipient = AccountRecipient.restore(recipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
    var accountRecipientEntity = seed(accountRecipient);

    var nonExistentRecipientId = UUID.randomUUID();
    var problemDetail =
        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", accountRecipient.getBankAccountId().value())
            .pathParam("recipient-id", nonExistentRecipientId)
            .when()
            .delete()
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .body()
            .as(ProblemDetail.class);

            var expectedMessageError = resolveMessage(ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE, DEFAULT_LOCALE, bankAccountId.value(), nonExistentRecipientId);
            assertAll(
                () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
                () -> assertThat(problemDetail.getTitle()).hasToString(ACCOUNT_RECIPIENT_NOT_FOUND_TITLE),
                () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
                () -> assertThat(problemDetail.getInstance())
                    .isEqualTo(uriProperties.accountRecipientURI(bankAccountId.value(), nonExistentRecipientId)));

    var key = buildRecipientKey(accountRecipientEntity);
    assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
        .as(
            "Recipient must remain because account recipient %s does not exist",
            nonExistentRecipientId)
        .isNotNull();
  }

  private AccountRecipientEntity seed(AccountRecipient accountRecipient) {
    var accountRecipientEntity = entityMapper.toEntity(accountRecipient);
    dynamoDbTable.putItem(accountRecipientEntity);
    return accountRecipientEntity;
  }

  private Key buildRecipientKey(AccountRecipientEntity entity) {
    return Key.builder()
        .partitionValue(entity.getPartitionKey())
        .sortValue(entity.getSortKey())
        .build();
  }

  private RequestSpecification buildRequestSpecification(int port) {
    return given()
        .baseUri("http://localhost")
        .port(port)
        .basePath(uriProperties.recipientIdPath())
        .header(HttpHeadersCustom.IDEMPOTENCY_KEY, UUID.randomUUID())
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON);
  }

  private String resolveMessage(String code, Locale locale, Object... args) {
      return messageResolverPort.resolveMessage(code, args, locale);
  }

  private RequestSpecification buildBaseRequestSpecification() {
    return new RequestSpecBuilder()
        .setBaseUri(RestAssured.baseURI)
        .setPort(RestAssured.port)
        .setBasePath(uriProperties.recipientIdPath())
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .build();
  }

  private RequestSpecification buildRequestSpecificationWithIdempotencyKey(IdempotencyKey idempotencyKey) {
    return new RequestSpecBuilder()
        .addRequestSpecification(buildBaseRequestSpecification())
        .addHeader(HttpHeadersCustom.IDEMPOTENCY_KEY, idempotencyKey.value().toString())
        .build();
  }
}
