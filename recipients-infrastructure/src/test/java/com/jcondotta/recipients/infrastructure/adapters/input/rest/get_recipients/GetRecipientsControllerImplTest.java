package com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients;

import com.jcondotta.recipients.application.usecase.get_recipients.GetRecipientsUseCase;
import com.jcondotta.recipients.application.usecase.get_recipients.model.RecipientDetails;
import com.jcondotta.recipients.application.usecase.get_recipients.model.query.GetRecipientsQuery;
import com.jcondotta.recipients.application.usecase.get_recipients.model.result.GetRecipientsResult;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.mapper.request.GetRecipientsRequestRestMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.mapper.response.GetRecipientsResponseMapper;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.request.GetRecipientsRestRequestParams;
import com.jcondotta.recipients.infrastructure.adapters.input.rest.get_recipients.model.response.GetRecipientsResponse;
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
class GetRecipientsControllerImplTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  @Mock
  private GetRecipientsUseCase useCase;

  @Mock
  private GetRecipientsRequestRestMapper requestMapper;

  @Mock
  private GetRecipientsResponseMapper responseMapper;

  @Mock
  private GetRecipientsRestRequestParams restRequestParams;

  @Mock
  private GetRecipientsQuery query;

  @Mock
  private GetRecipientsResponse response;

  private GetRecipientsControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller =
        new GetRecipientsControllerImpl(useCase, requestMapper, responseMapper);
  }

  @Test
  void shouldReturnNoContent_whenNoRecipientsAreFound() {
    var result = GetRecipientsResult.of(List.of(), null);

    when(requestMapper.toQuery(BANK_ACCOUNT_ID, restRequestParams)).thenReturn(query);
    when(useCase.execute(query)).thenReturn(result);

    ResponseEntity<GetRecipientsResponse> responseEntity =
        controller.byQuery(BANK_ACCOUNT_ID, restRequestParams);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(responseEntity.getBody()).isNull();

    verify(requestMapper).toQuery(BANK_ACCOUNT_ID, restRequestParams);
    verify(useCase).execute(query);
    verifyNoInteractions(responseMapper);

    verifyNoMoreInteractions(requestMapper, useCase);
  }

  @Test
  void shouldReturnOkWithResponseBody_whenRecipientsAreFound() {
    var recipients = List.of(mock(RecipientDetails.class));
    var nextCursor = "next-cursor";
    var result = GetRecipientsResult.of(recipients, nextCursor);

    when(requestMapper.toQuery(BANK_ACCOUNT_ID, restRequestParams)).thenReturn(query);
    when(useCase.execute(query)).thenReturn(result);
    when(responseMapper.toResponse(recipients, nextCursor)).thenReturn(response);

    ResponseEntity<GetRecipientsResponse> responseEntity =
        controller.byQuery(BANK_ACCOUNT_ID, restRequestParams);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
    assertThat(responseEntity.getBody()).isEqualTo(response);

    verify(requestMapper).toQuery(BANK_ACCOUNT_ID, restRequestParams);
    verify(useCase).execute(query);
    verify(responseMapper).toResponse(recipients, nextCursor);

    verifyNoMoreInteractions(requestMapper, useCase, responseMapper);
  }
}
