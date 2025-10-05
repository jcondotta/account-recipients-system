package com.jcondotta.account_recipients.get_recipients.controller;

import com.jcondotta.account_recipients.get_recipients.controller.model.response.GetAccountRecipientsResponse;
import com.jcondotta.account_recipients.get_recipients.controller.model.request.GetAccountRecipientsRestRequestParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.root-path}")
public interface GetAccountRecipientsController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetAccountRecipientsResponse> byQuery(
        @PathVariable("bank-account-id") UUID bankAccountId,
        @ModelAttribute GetAccountRecipientsRestRequestParams restRequestParams
    );
}