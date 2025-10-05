package com.jcondotta.account_recipients.create_recipient.controller;

import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import com.jcondotta.account_recipients.common.argument_provider.BlankValuesArgumentProvider;
import com.jcondotta.account_recipients.common.container.LocalStackTestContainer;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import com.jcondotta.account_recipients.create_recipient.controller.model.CreateAccountRecipientRestRequest;
import com.jcondotta.account_recipients.interfaces.rest.exception_handler.ProblemTypes;
import com.jcondotta.account_recipients.interfaces.rest.headers.HttpHeadersCustom;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
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
import org.springframework.web.servlet.LocaleResolver;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE;
import static com.jcondotta.account_recipients.domain.bank_account.exceptions.BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TITLE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = { LocalStackTestContainer.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CreateAccountRecipientControllerImplIT {

    @Autowired
    private AccountRecipientURIProperties uriProperties;

    @Autowired
    private MessageResolverPort messageResolverPort;

    @Autowired
    private Clock fixedClock;

    @Autowired
    private LocaleResolver localeResolver;

    private Locale defaultLocale;

    private UUID bankAccountId;
    private String recipientName;
    private String iban;

    private RequestSpecification requestSpecification;

    @BeforeAll
    static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(@LocalServerPort int port) {
        bankAccountId = UUID.randomUUID();
        recipientName = AccountRecipientFixtures.JEFFERSON.getRecipientName();
        iban = AccountRecipientFixtures.JEFFERSON.getRecipientIban();

        requestSpecification = buildRequestSpecification(port);
        defaultLocale = localeResolver.resolveLocale(new MockHttpServletRequest());
    }

    @Test
    void shouldReturn201Created_whenRequestIsValid() {
        stubFor(get(urlPathMatching("/api/v1/bank-accounts/" + bankAccountId))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("bank-accounts/bank-account-active.json")));

        var restRequest = CreateAccountRecipientRestRequest.of(recipientName, iban);
        var expectedLocationURI = uriProperties.accountRecipientsURI(bankAccountId).getRawPath();

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .body(restRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .header("location", equalTo(expectedLocationURI));
    }

    @Test
    void shouldReturn404NotFound_whenBankAccountIsNotFound() {
        stubFor(get(urlPathMatching("/api/v1/bank-accounts/" + bankAccountId))
            .willReturn(aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value()))
        );

        var restRequest = CreateAccountRecipientRestRequest.of(recipientName, iban);

        var problemDetail = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .body(restRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .body()
            .as(ProblemDetail.class);

        var expectedMessageError = messageResolverPort.resolveMessage(BANK_ACCOUNT_NOT_FOUND_TEMPLATE, new Object[]{ bankAccountId }, defaultLocale);
        assertAll(
            () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RESOURCE_NOT_FOUND.toString()),
            () -> assertThat(problemDetail.getTitle()).hasToString(BANK_ACCOUNT_NOT_FOUND_TITLE),
            () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
            () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientsURI(bankAccountId))
        );
    }

    @Test
    void shouldReturn500InternalServerError_whenBankAccountAPIFails() {
        stubFor(get(urlPathMatching("/api/v1/bank-accounts/" + bankAccountId))
                .willReturn(aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        );

        var restRequest = CreateAccountRecipientRestRequest.of(recipientName, iban);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .body(restRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldReturn422UnprocessableEntity_whenRecipientNameIsBlank(String invalidRecipientName) {
        var restRequest = CreateAccountRecipientRestRequest.of(invalidRecipientName, iban);

        var problemDetail = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .body(restRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .extract()
                .body()
                .as(ProblemDetail.class);

        assertAll(
            () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
            () -> assertThat(problemDetail.getTitle()).hasToString("Request validation failed"),
            () -> assertThat(problemDetail.getStatus()).isEqualTo(422),
            () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientsURI(bankAccountId))
        );
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldReturn422UnprocessableEntity_whenIbanIsBlank(String invalidIban) {
        var restRequest = CreateAccountRecipientRestRequest.of(recipientName, invalidIban);

        var problemDetail = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .body(restRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .extract()
                .body()
                .as(ProblemDetail.class);

        assertAll(
            () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
            () -> assertThat(problemDetail.getTitle()).hasToString("Request validation failed"),
            () -> assertThat(problemDetail.getStatus()).isEqualTo(422),
            () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.accountRecipientsURI(bankAccountId))
        );
    }

    private RequestSpecification buildRequestSpecification(int port) {
        return given()
            .baseUri("http://localhost")
            .port(port)
            .basePath(uriProperties.rootPath())
            .header(HttpHeadersCustom.IDEMPOTENCY_KEY, UUID.randomUUID())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
    }
}