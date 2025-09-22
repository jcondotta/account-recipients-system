package com.jcondotta.account_recipients.interfaces.rest.exception_handler;

import com.jcondotta.account_recipients.application.ports.output.i18n.MessageResolverPort;
import com.jcondotta.account_recipients.domain.shared.exceptions.DomainObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ResourceNotFoundExceptionHandler {

    private final MessageResolverPort messageResolverPort;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DomainObjectNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(DomainObjectNotFoundException ex, HttpServletRequest request, Locale locale) {
        var message = messageResolverPort.resolveMessage(ex.getMessage(), ex.getIdentifiers(), locale)
            .orElse(ex.getMessage());

        var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://api.jcondotta.com/problems/resource-not-found"));
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setDetail(message);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND.value())
            .body(problemDetail);
    }
}
