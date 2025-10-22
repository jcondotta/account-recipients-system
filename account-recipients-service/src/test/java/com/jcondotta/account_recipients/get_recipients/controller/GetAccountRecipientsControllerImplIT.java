package com.jcondotta.account_recipients.get_recipients.controller;

import com.jcondotta.account_recipients.application.ports.output.cache.AccountRecipientsQueryCacheKey;
import com.jcondotta.account_recipients.application.ports.output.cache.CacheStore;
import com.jcondotta.account_recipients.application.ports.output.repository.get_recipients.model.GetAccountRecipientsQueryParams;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.AccountRecipientDetails;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetAccountRecipientsResult;
import com.jcondotta.account_recipients.common.container.LocalStackTestContainer;
import com.jcondotta.account_recipients.common.container.RedisTestContainer;
import com.jcondotta.account_recipients.common.factory.AccountRecipientEntityTestFactory;
import com.jcondotta.account_recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.account_recipients.domain.shared.value_objects.BankAccountId;
import com.jcondotta.account_recipients.infrastructure.adapters.output.repository.entity.AccountRecipientEntity;
import com.jcondotta.account_recipients.infrastructure.properties.AccountRecipientURIProperties;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.AccountRecipientResponse;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.GetAccountRecipientsResponse;
import com.jcondotta.account_recipients.infrastructure.interfaces.rest.headers.HttpHeadersCustom;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.UUID;

import static com.jcondotta.account_recipients.common.fixtures.AccountRecipientFixtures.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = { LocalStackTestContainer.class, RedisTestContainer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAccountRecipientsControllerImplIT {

    @Autowired
    private DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;

    @Autowired
    private AccountRecipientURIProperties uriProperties;

    @Autowired
    private CacheStore<GetAccountRecipientsResult> cacheStore;

    private RequestSpecification requestSpecification;

    private UUID bankAccountId;
    private AccountRecipientEntity recipientJefferson;
    private AccountRecipientEntity recipientPatrizio;
    private AccountRecipientEntity recipientVirginio;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(@LocalServerPort int port) {
        requestSpecification = buildRequestSpecification(port);

        bankAccountId = UUID.randomUUID();
        recipientJefferson = AccountRecipientEntityTestFactory.create(bankAccountId, JEFFERSON.getRecipientName());
        recipientPatrizio = AccountRecipientEntityTestFactory.create(bankAccountId, PATRIZIO.getRecipientName());
        recipientVirginio = AccountRecipientEntityTestFactory.create(bankAccountId, VIRGINIO.getRecipientName());
    }

    @Nested
    class Pagination {

        @Test
        void shouldReturnFirstPageAndNextCursor_whenMultiplePagesAvailable() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);
            accountRecipientsTable.putItem(recipientVirginio);

            var pageLimit = 2;

            var responsePage1 = given()
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

            assertThat(responsePage1.accountRecipients())
                .hasSize(pageLimit)
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

            assertThat(responsePage1.nextCursor()).isNotNull();

            var responsePage2 = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("limit", pageLimit)
                .queryParam("cursor", responsePage1.nextCursor())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .as(GetAccountRecipientsResponse.class);

            assertThat(responsePage2.accountRecipients())
                .hasSize(1)
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactly(VIRGINIO.getRecipientName());

            assertThat(responsePage2.nextCursor()).isNull();
        }

        @Test
        void shouldReturnFirstPageWithNextCursor_whenItemsExceedLimit() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);
            accountRecipientsTable.putItem(recipientVirginio);

            var pageLimit = 2;
            var response = given()
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

            assertThat(response.accountRecipients())
                .hasSize(pageLimit)
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

            assertThat(response.nextCursor()).isNotNull();
        }

        @Test
        void shouldReturnAllItemsAndNullCursor_whenItemsExactlyFillPageLimit() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);

            var pageLimit = 2;
            var response = given()
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

            assertThat(response.accountRecipients())
                .hasSize(pageLimit)
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

            assertThat(response.nextCursor()).isNull();
        }

        @Test
        void shouldApplyDefaultLimitAndReturnNextCursor_whenLimitParamIsOmitted() {
            for (int i = 0; i < 12; i++) {
                var recipient = AccountRecipientEntityTestFactory.create(bankAccountId, "Recipient #" + i);
                accountRecipientsTable.putItem(recipient);
            }

            var response = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .as(GetAccountRecipientsResponse.class);

            assertThat(response.accountRecipients())
                .hasSize(GetAccountRecipientsQueryParams.DEFAULT_LIMIT)
                .extracting(AccountRecipientResponse::recipientName)
                .allSatisfy(name -> assertThat(name)
                    .startsWith("Recipient #"));

            assertThat(response.nextCursor()).isNotBlank();

            var responsePage2 = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("cursor", response.nextCursor())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .as(GetAccountRecipientsResponse.class);

            assertThat(responsePage2.accountRecipients())
                .hasSize(2)
                .extracting(AccountRecipientResponse::recipientName)
                .allSatisfy(name -> assertThat(name).startsWith("Recipient #"));

            assertThat(responsePage2.nextCursor()).isNull();
        }
    }

    @Nested
    class Cursor {

        @Test
        void shouldReturnAllItemsAndNullCursor_whenLastPageIsReached() {
            accountRecipientsTable.putItem(recipientVirginio);
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);

            var pageLimit = 3;
            var response = given()
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

            assertThat(response.accountRecipients())
                .hasSize(pageLimit)
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName(), VIRGINIO.getRecipientName());

            assertThat(response.nextCursor()).isNull();
        }

        @Test
        void shouldReturnNoContent_whenCursorBelongsToAnotherBankAccount() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);

            var pageLimit = 1;

            var response1 = given()
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

            assertThat(response1.accountRecipients()).hasSize(pageLimit);
            assertThat(response1.nextCursor()).isNotBlank();

            var nonExistingBankAccountId = UUID.randomUUID();

            given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", nonExistingBankAccountId)
                .queryParam("limit", pageLimit)
                .queryParam("cursor", response1.nextCursor())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    class Cache {

        @Test
        void shouldPopulateCache_whenQueryIsExecuted() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);

            var pageLimit = 2;
            given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("limit", pageLimit)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value());

            var queryParams = GetAccountRecipientsQueryParams.of(2);
            var queryCacheKey = AccountRecipientsQueryCacheKey.of(BankAccountId.of(bankAccountId), queryParams);

            assertThat(cacheStore.getIfPresent(queryCacheKey.value()))
                .as("Expected cache to be populated after first query")
                .hasValueSatisfying(accountRecipientsResult -> {
                    assertThat(accountRecipientsResult.nextCursor()).isBlank();
                    assertThat(accountRecipientsResult.accountRecipients())
                        .hasSize(2)
                        .extracting(AccountRecipientDetails::recipientName)
                        .map(RecipientName::value)
                        .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());
                });
        }

        @Test
        void shouldReturnSameResultFromCache_whenQueryIsExecutedTwice() {
            accountRecipientsTable.putItem(recipientJefferson);
            accountRecipientsTable.putItem(recipientPatrizio);

            var pageLimit = 2;
            var recipientsResponse1 = given()
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

            var recipientsResponse2 = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .queryParam("limit", 2)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .as(GetAccountRecipientsResponse.class);

            assertThat(recipientsResponse1.nextCursor()).isBlank();
            assertThat(recipientsResponse2.nextCursor()).isBlank();
            assertThat(recipientsResponse2.accountRecipients())
                .extracting(AccountRecipientResponse::recipientName)
                .containsExactlyElementsOf(
                    recipientsResponse1.accountRecipients().stream()
                        .map(AccountRecipientResponse::recipientName)
                        .toList()
                );

            var queryParams = GetAccountRecipientsQueryParams.of(2);
            var queryCacheKey = AccountRecipientsQueryCacheKey.of(BankAccountId.of(bankAccountId), queryParams);

            assertThat(cacheStore.getIfPresent(queryCacheKey.value())).isPresent();
        }
    }

    @Nested
    class EmptyResults {

        @Test
        void shouldReturnNoContent_whenNoAccountRecipientsAreFound() {
            given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }
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