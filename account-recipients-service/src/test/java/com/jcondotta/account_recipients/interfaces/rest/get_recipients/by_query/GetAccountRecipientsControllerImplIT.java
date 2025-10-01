package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query;

import com.jcondotta.account_recipients.common.container.LocalStackTestContainer;
import com.jcondotta.account_recipients.common.factory.AccountRecipientEntityTestFactory;
import com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model.AccountRecipientResponse;
import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_query.model.GetAccountRecipientsResponse;
import com.jcondotta.account_recipients.interfaces.rest.headers.HttpHeadersCustom;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = { LocalStackTestContainer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAccountRecipientsControllerImplIT {

    private static final String RECIPIENT_NAME_JEFFERSON = JEFFERSON.getRecipientName();
    private static final String RECIPIENT_NAME_VIRGINIO = VIRGINIO.getRecipientName();
    private static final String RECIPIENT_NAME_PATRIZIO = PATRIZIO.getRecipientName();

    @Autowired
    private DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;

    @Autowired
    private AccountRecipientURIProperties uriProperties;

    @Autowired
    private Clock fixedClock;

    private RequestSpecification requestSpecification;

    private UUID bankAccountId;
    private AccountRecipientEntity recipientJefferson;
    private AccountRecipientEntity recipientPatrizio;
    private AccountRecipientEntity recipientVirginio;

    @BeforeAll
    static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(@LocalServerPort int port) {
        requestSpecification = buildRequestSpecification(port);

        bankAccountId = UUID.randomUUID();
        recipientJefferson = AccountRecipientEntityTestFactory.create(bankAccountId, RECIPIENT_NAME_JEFFERSON);
        recipientPatrizio = AccountRecipientEntityTestFactory.create(bankAccountId, RECIPIENT_NAME_VIRGINIO);
        recipientVirginio = AccountRecipientEntityTestFactory.create(bankAccountId, RECIPIENT_NAME_PATRIZIO);
    }

    @Test
    void should200OkWithPaginatedItems_whenBankAccountHasNoRecipients() {
        accountRecipientsTable.putItem(recipientVirginio);
        accountRecipientsTable.putItem(recipientJefferson);
        accountRecipientsTable.putItem(recipientPatrizio);

        var pageLimit = 2;
        var getAccountRecipientsResponse = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("limit", pageLimit)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(GetAccountRecipientsResponse.class);

        assertThat(getAccountRecipientsResponse.accountRecipients())
            .hasSize(pageLimit)
            .extracting(AccountRecipientResponse::recipientName)
            .containsExactly(RECIPIENT_NAME_JEFFERSON, RECIPIENT_NAME_PATRIZIO);
    }

    @Test
    void should200OkWithPaginatedItems_whenBankAccountHasNoRecipients2() {
        accountRecipientsTable.putItem(recipientVirginio);
        accountRecipientsTable.putItem(recipientJefferson);
        accountRecipientsTable.putItem(recipientPatrizio);

        var pageLimit = 3;
        var getAccountRecipientsResponse = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("limit", pageLimit)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(GetAccountRecipientsResponse.class);

        assertThat(getAccountRecipientsResponse.accountRecipients())
            .hasSize(pageLimit)
            .extracting(AccountRecipientResponse::recipientName)
            .containsExactly(RECIPIENT_NAME_JEFFERSON, RECIPIENT_NAME_PATRIZIO, RECIPIENT_NAME_VIRGINIO);

        assertThat(getAccountRecipientsResponse.nextCursor())
            .isNotNull();
    }

    @Test
    void should204NoContent_whenBankAccountHasNoRecipients() {
        var bankAccountId = UUID.randomUUID();

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
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