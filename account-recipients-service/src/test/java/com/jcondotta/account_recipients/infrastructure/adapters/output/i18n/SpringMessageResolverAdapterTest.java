package com.jcondotta.account_recipients.infrastructure.adapters.output.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringMessageResolverAdapterTest {

  private static final Locale LOCALE = Locale.ENGLISH;
  private static final String MESSAGE_CODE = "error.recipient.not.found";

  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private SpringMessageResolverAdapter messageResolver;

  @Test
  void shouldResolveMessageSuccessfully_whenArgsAreProvided() {
    Object[] args = {"Jefferson"};
    String resolvedMessage = "Recipient Jefferson not found";

    when(messageSource.getMessage(MESSAGE_CODE, args, MESSAGE_CODE, LOCALE))
        .thenReturn(resolvedMessage);

    String result =
        messageResolver.resolveMessage(MESSAGE_CODE, args, LOCALE);

    assertThat(result).isEqualTo(resolvedMessage);

    verify(messageSource).getMessage(MESSAGE_CODE, args, MESSAGE_CODE, LOCALE);
    verifyNoMoreInteractions(messageSource);
  }

  @Test
  void shouldResolveMessageSuccessfully_whenArgsAreNull() {
    String resolvedMessage = "Recipient not found";

    when(messageSource.getMessage(MESSAGE_CODE, new Object[0], MESSAGE_CODE, LOCALE))
        .thenReturn(resolvedMessage);

    String result =
        messageResolver.resolveMessage(MESSAGE_CODE, null, LOCALE);

    assertThat(result).isEqualTo(resolvedMessage);

    verify(messageSource).getMessage(MESSAGE_CODE, new Object[0], MESSAGE_CODE, LOCALE);
    verifyNoMoreInteractions(messageSource);
  }

  @Test
  void shouldReturnMessageCodeAsDefault_whenMessageIsNotFound() {
    when(messageSource.getMessage(MESSAGE_CODE, new Object[0], MESSAGE_CODE, LOCALE))
        .thenReturn(MESSAGE_CODE);

    String result =
        messageResolver.resolveMessage(MESSAGE_CODE, null, LOCALE);

    assertThat(result).isEqualTo(MESSAGE_CODE);

    verify(messageSource).getMessage(MESSAGE_CODE, new Object[0], MESSAGE_CODE, LOCALE);
    verifyNoMoreInteractions(messageSource);
  }

  @Test
  void shouldThrowNullPointerException_whenMessageCodeIsNull() {
    assertThatThrownBy(() ->
        messageResolver.resolveMessage(null, null, LOCALE))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Message code must not be null");

    verifyNoInteractions(messageSource);
  }

  @Test
  void shouldThrowNullPointerException_whenLocaleIsNull() {
    assertThatThrownBy(() ->
        messageResolver.resolveMessage(MESSAGE_CODE, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");

    verifyNoInteractions(messageSource);
  }
}
