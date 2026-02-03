package com.jcondotta.recipients.infrastructure.interfaces.rest.exception_handler;

import com.jcondotta.recipients.application.ports.output.i18n.MessageResolverPort;
import com.jcondotta.recipients.domain.exceptions.DomainBusinessRuleViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class BusinessRuleExceptionHandler {

  private final MessageResolverPort messageResolverPort;

  @ExceptionHandler(DomainBusinessRuleViolationException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(
      DomainBusinessRuleViolationException ex,
      HttpServletRequest request,
      Locale locale
  ) {
    var message = messageResolverPort.resolveMessage(ex.messageCode(), ex.args(), locale);

    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    problemDetail.setType(ProblemTypes.BUSINESS_RULE_VIOLATION);
    problemDetail.setTitle(ex.title());
    problemDetail.setDetail(message);
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(problemDetail);
  }
}
