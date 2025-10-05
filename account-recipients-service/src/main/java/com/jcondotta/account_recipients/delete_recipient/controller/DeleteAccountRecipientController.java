package com.jcondotta.account_recipients.delete_recipient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.account-recipient-id-path}")
public interface DeleteAccountRecipientController {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteAccountRecipient(
        @PathVariable("bank-account-id") UUID bankAccountId,
        @PathVariable("account-recipient-id") UUID accountRecipientId
    );
}