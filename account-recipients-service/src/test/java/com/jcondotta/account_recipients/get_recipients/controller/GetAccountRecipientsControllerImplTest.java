package com.jcondotta.account_recipients.get_recipients.controller;

import com.jcondotta.account_recipients.application.usecase.get_recipients.GetRecipientsUseCase;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.account_recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.account_recipients.get_recipients.controller.mapper.request.GetAccountRecipientsRequestRestMapper;
import com.jcondotta.account_recipients.get_recipients.controller.mapper.response.GetAccountRecipientsResponseMapper;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import com.jcondotta.account_recipients.get_recipients.controller.model.response.GetAccountRecipientsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountRecipientsControllerImplTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  @Mock
  private GetRecipientsUseCase useCase;

  @Mock
  private GetAccountRecipientsRequestRestMapper requestMapper;

  @Mock
  private GetAccountRecipientsResponseMapper responseMapper;

  @Mock
  private GetAccountRecipientsRestRequestParams restRequestParams;

  @Mock
  private GetRecipientsQuery query;

  @Mock
  private GetAccountRecipientsResponse response;

  private GetAccountRecipientsControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller =
        new GetAccountRecipientsControllerImpl(useCase, requestMapper, responseMapper);
  }

  @Test
  void shouldReturnNoContent_whenNoAccountRecipientsAreFound() {
    var result = GetRecipientsResult.of(List.of(), null);

    when(requestMapper.toQuery(BANK_ACCOUNT_ID, restRequestParams)).thenReturn(query);
    when(useCase.execute(query)).thenReturn(result);

    ResponseEntity<GetAccountRecipientsResponse> responseEntity =
        controller.byQuery(BANK_ACCOUNT_ID, restRequestParams);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(responseEntity.getBody()).isNull();

    verify(requestMapper).toQuery(BANK_ACCOUNT_ID, restRequestParams);
    verify(useCase).execute(query);
    verifyNoInteractions(responseMapper);

    verifyNoMoreInteractions(requestMapper, useCase);
  }

  @Test
  void shouldReturnOkWithResponseBody_whenAccountRecipientsAreFound() {
    var recipients = List.of(mock(RecipientDetails.class));
    var nextCursor = "next-cursor";
    var result = GetRecipientsResult.of(recipients, nextCursor);

    when(requestMapper.toQuery(BANK_ACCOUNT_ID, restRequestParams)).thenReturn(query);
    when(useCase.execute(query)).thenReturn(result);
    when(responseMapper.toResponse(recipients, nextCursor)).thenReturn(response);

    ResponseEntity<GetAccountRecipientsResponse> responseEntity =
        controller.byQuery(BANK_ACCOUNT_ID, restRequestParams);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
    assertThat(responseEntity.getBody()).isEqualTo(response);

    verify(requestMapper).toQuery(BANK_ACCOUNT_ID, restRequestParams);
    verify(useCase).execute(query);
    verify(responseMapper).toResponse(recipients, nextCursor);

    verifyNoMoreInteractions(requestMapper, useCase, responseMapper);
  }
}
