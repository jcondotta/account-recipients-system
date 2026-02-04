package com.jcondotta.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<String> handleMissingHeader(MissingRequestHeaderException ex) {
    var headerName = ex.getHeaderName();

    log.warn("Request missing required header: {}", ex.getHeaderName());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Required header '" + headerName + "' is missing.");
  }
}
