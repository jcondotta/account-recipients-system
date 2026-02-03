package com.jcondotta.recipients.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountStatusTest {

  @Test
  void shouldReturnTrueForIsActive_whenStatusIsActive() {
    assertThat(AccountStatus.ACTIVE.isActive()).isTrue();
  }

  @Test
  void shouldReturnTrueForIsPending_whenStatusIsPending() {
    assertThat(AccountStatus.PENDING.isPending()).isTrue();
  }

  @Test
  void shouldReturnTrueForIsCancelled_whenStatusIsCancelled() {
    assertThat(AccountStatus.CANCELLED.isCancelled()).isTrue();
  }

  @Test
  void shouldReturnFalseForAll_whenStatusIsUnknown() {
    assertThat(AccountStatus.UNKNOWN.isActive()).isFalse();
    assertThat(AccountStatus.UNKNOWN.isPending()).isFalse();
    assertThat(AccountStatus.UNKNOWN.isCancelled()).isFalse();
  }
}