package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients;

import com.jcondotta.recipients.application.ports.output.repository.get_recipients.model.GetRecipientsQueryParams;
import com.jcondotta.recipients.common.container.LocalStackTestContainer;
import com.jcondotta.recipients.common.factory.RecipientEntityTestFactory;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response.GetRecipientsResponse;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response.RecipientResponse;
import com.jcondotta.recipients.infrastructure.adapters.output.repository.entity.RecipientEntity;
import com.jcondotta.recipients.infrastructure.properties.RecipientURIProperties;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.UUID;

import static com.jcondotta.recipients.common.fixtures.RecipientFixtures.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = {LocalStackTestContainer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetRecipientsControllerImplIT {

  @Autowired
  private DynamoDbTable<RecipientEntity> recipientsTable;

  @Autowired
  private RecipientURIProperties uriProperties;

  private RequestSpecification requestSpecification;

  private UUID bankAccountId;
  private RecipientEntity recipientJefferson;
  private RecipientEntity recipientPatrizio;
  private RecipientEntity recipientVirginio;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    requestSpecification = buildRequestSpecification();

    bankAccountId = UUID.randomUUID();
    recipientJefferson =
        RecipientEntityTestFactory.create(bankAccountId, JEFFERSON.getRecipientName());
    recipientPatrizio =
        RecipientEntityTestFactory.create(bankAccountId, PATRIZIO.getRecipientName());
    recipientVirginio =
        RecipientEntityTestFactory.create(bankAccountId, VIRGINIO.getRecipientName());
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
        .setBaseUri(RestAssured.baseURI)
        .setPort(RestAssured.port)
        .setBasePath(uriProperties.rootPath())
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .build();
  }

  @Nested
  class Pagination {

    @Test
    void shouldReturnFirstPageAndNextCursor_whenMultiplePagesAvailable() {
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);
      recipientsTable.putItem(recipientVirginio);

      var pageLimit = 2;

      var responsePage1 =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(responsePage1.recipients())
          .hasSize(pageLimit)
          .extracting(RecipientResponse::recipientName)
          .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

      assertThat(responsePage1.nextCursor()).isNotNull();

      var responsePage2 =
          given()
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
              .as(GetRecipientsResponse.class);

      assertThat(responsePage2.recipients())
          .hasSize(1)
          .extracting(RecipientResponse::recipientName)
          .containsExactly(VIRGINIO.getRecipientName());

      assertThat(responsePage2.nextCursor()).isNull();
    }

    @Test
    void shouldReturnFirstPageWithNextCursor_whenItemsExceedLimit() {
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);
      recipientsTable.putItem(recipientVirginio);

      var pageLimit = 2;
      var response =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(response.recipients())
          .hasSize(pageLimit)
          .extracting(RecipientResponse::recipientName)
          .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

      assertThat(response.nextCursor()).isNotNull();
    }

    @Test
    void shouldReturnAllItemsAndNullCursor_whenItemsExactlyFillPageLimit() {
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);

      var pageLimit = 2;
      var response =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(response.recipients())
          .hasSize(pageLimit)
          .extracting(RecipientResponse::recipientName)
          .containsExactly(JEFFERSON.getRecipientName(), PATRIZIO.getRecipientName());

      assertThat(response.nextCursor()).isNull();
    }

    @Test
    void shouldApplyDefaultLimitAndReturnNextCursor_whenLimitParamIsOmitted() {
      bankAccountId = UUID.randomUUID();
      var numbersOfRecipients = GetRecipientsQueryParams.DEFAULT_LIMIT + 2;

      for (int i = 0; i < numbersOfRecipients; i++) {
        var recipient = RecipientEntityTestFactory.create(bankAccountId, "Recipient #" + i);
        recipientsTable.putItem(recipient);
      }

      var response =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(response.recipients())
          .hasSize(GetRecipientsQueryParams.DEFAULT_LIMIT)
          .extracting(RecipientResponse::recipientName)
          .allSatisfy(name -> assertThat(name).startsWith("Recipient #"));

      assertThat(response.nextCursor()).isNotBlank();

      var responsePage2 =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("cursor", response.nextCursor())
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(responsePage2.recipients())
          .hasSize(2)
          .extracting(RecipientResponse::recipientName)
          .allSatisfy(name -> assertThat(name).startsWith("Recipient #"));

      assertThat(responsePage2.nextCursor()).isNull();
    }
  }

  @Nested
  class Cursor {

    @Test
    void shouldReturnAllItemsAndNullCursor_whenLastPageIsReached() {
      recipientsTable.putItem(recipientVirginio);
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);

      var pageLimit = 3;
      var response =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(response.recipients())
          .hasSize(pageLimit)
          .extracting(RecipientResponse::recipientName)
          .containsExactly(
              JEFFERSON.getRecipientName(),
              PATRIZIO.getRecipientName(),
              VIRGINIO.getRecipientName());

      assertThat(response.nextCursor()).isNull();
    }

    @Test
    void shouldReturnNoContent_whenCursorBelongsToAnotherBankAccount() {
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);

      var pageLimit = 1;

      var response1 =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(response1.recipients()).hasSize(pageLimit);
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
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);

      var pageLimit = 2;
      given()
          .spec(requestSpecification)
          .pathParam("bank-account-id", bankAccountId)
          .queryParam("limit", pageLimit)
          .when()
          .get()
          .then()
          .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnSameResultFromCache_whenQueryIsExecutedTwice() {
      recipientsTable.putItem(recipientJefferson);
      recipientsTable.putItem(recipientPatrizio);

      var pageLimit = 2;
      var recipientsResponse1 =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", pageLimit)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      var recipientsResponse2 =
          given()
              .spec(requestSpecification)
              .pathParam("bank-account-id", bankAccountId)
              .queryParam("limit", 2)
              .when()
              .get()
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .body()
              .as(GetRecipientsResponse.class);

      assertThat(recipientsResponse1.nextCursor()).isBlank();
      assertThat(recipientsResponse2.nextCursor()).isBlank();
      assertThat(recipientsResponse2.recipients())
          .extracting(RecipientResponse::recipientName)
          .containsExactlyElementsOf(
              recipientsResponse1.recipients().stream()
                  .map(RecipientResponse::recipientName)
                  .toList());
    }
  }

  @Nested
  class EmptyResults {

    @Test
    void shouldReturnNoContent_whenNoRecipientsAreFound() {
      given()
          .spec(requestSpecification)
          .pathParam("bank-account-id", bankAccountId)
          .when()
          .get()
          .then()
          .statusCode(HttpStatus.NO_CONTENT.value());
    }
  }
}
