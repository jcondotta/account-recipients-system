package com.jcondotta.account_recipients.delete_recipient.controller;

import com.jcondotta.account_recipients.application.ports.output.cache.AccountRecipientsRootCacheKey;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.common.container.LocalStackTestContainer;
import com.jcondotta.account_recipients.common.container.RedisTestContainer;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.domain.recipient.entity.AccountRecipient;
import com.jcondotta.account_recipients.domain.recipient.value_objects.AccountRecipientId;
import com.jcondotta.account_recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.mapper.AccountRecipientEntityMapper;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import com.jcondotta.account_recipients.infrastructure.interfaces.rest.headers.HttpHeadersCustom;
import io.restassured.RestAssured;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class, RedisTestContainer.class})
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
    private CacheStore<GetAccountRecipientsResult> cacheStore;

    private AccountRecipientId accountRecipientId;
    private BankAccountId bankAccountId;
    private RecipientName recipientName;
    private Iban iban;

    private ZonedDateTime fixedZonedDateTime;

    private RequestSpecification requestSpecification;

    @BeforeAll
    static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(@LocalServerPort int port) {
        accountRecipientId = AccountRecipientId.of(UUID.randomUUID());
        bankAccountId = BankAccountId.of(UUID.randomUUID());
        recipientName = RecipientName.of(AccountRecipientFixtures.JEFFERSON.getRecipientName());
        iban = Iban.of(AccountRecipientFixtures.JEFFERSON.getRecipientIban());
        fixedZonedDateTime = ZonedDateTime.now(fixedClock);

        requestSpecification = buildRequestSpecification(port);
    }

    @Test
    void shouldReturn204NoContent_whenAccountRecipientIsFound() {
        var accountRecipient = AccountRecipient.of(accountRecipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
        var accountRecipientEntity = seed(accountRecipient);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", accountRecipient.bankAccountId().value())
                .pathParam("account-recipient-id", accountRecipient.accountRecipientId().value())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        var key = buildRecipientKey(accountRecipientEntity);
        assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
            .as("The account recipient entity should be deleted from the database")
            .isNull();

        var cacheKey = String.format(AccountRecipientsRootCacheKey.PREFIX_TEMPLATE, accountRecipient.bankAccountId());
        assertThat(cacheStore.getIfPresent(cacheKey)).isEmpty();
    }

    @Test
    void shouldReturn404NotFound_whenBankAccountIsNotFound() {
        var accountRecipient = AccountRecipient.of(accountRecipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
        var accountRecipientEntity = seed(accountRecipient);

        var nonExistentBankAccountId = UUID.randomUUID();
        var problemDetail = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", nonExistentBankAccountId)
                .pathParam("account-recipient-id", accountRecipient.accountRecipientId().value())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .body()
                .as(ProblemDetail.class);

//        var expectedMessageError = resolveMessage(ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE, DEFAULT_LOCALE, nonExistentBankAccountId, accountRecipientId.value());
//        assertAll(
//            () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
//            () -> assertThat(problemDetail.getTitle()).hasToString(ACCOUNT_RECIPIENT_NOT_FOUND_TITLE),
//            () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
//            () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientURI(nonExistentBankAccountId, accountRecipientId.value()))
//        );

        var key = buildRecipientKey(accountRecipientEntity);
        assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
            .as("Recipient must remain because bank account %s does not exist", nonExistentBankAccountId)
            .isNotNull();
    }

    @Test
    void shouldReturn404NotFound_whenBankAccountExistsButAccountRecipientDoesNot() {
        var accountRecipient = AccountRecipient.of(accountRecipientId, bankAccountId, recipientName, iban, fixedZonedDateTime);
        var accountRecipientEntity = seed(accountRecipient);

        var nonExistentAccountRecipientId = UUID.randomUUID();
        var problemDetail = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", accountRecipient.bankAccountId().value())
                .pathParam("account-recipient-id", nonExistentAccountRecipientId)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .body()
                .as(ProblemDetail.class);

//        var expectedMessageError = resolveMessage(ACCOUNT_RECIPIENT_NOT_FOUND_TEMPLATE, DEFAULT_LOCALE, bankAccountId.value(), nonExistentAccountRecipientId);
//        assertAll(
//            () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
//            () -> assertThat(problemDetail.getTitle()).hasToString(ACCOUNT_RECIPIENT_NOT_FOUND_TITLE),
//            () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
//            () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientURI(bankAccountId.value(), nonExistentAccountRecipientId))
//        );

        var key = buildRecipientKey(accountRecipientEntity);
        assertThat(dynamoDbTable.getItem(r -> r.key(key).consistentRead(true)))
            .as("Recipient must remain because account recipient %s does not exist", nonExistentAccountRecipientId)
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
            .basePath(uriProperties.accountRecipientIdPath())
            .header(HttpHeadersCustom.IDEMPOTENCY_KEY, UUID.randomUUID())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
    }

//    private String resolveMessage(String code, Locale locale, Object... args) {
//        return messageResolverPort.resolveMessage(code, args, locale);
//    }
}