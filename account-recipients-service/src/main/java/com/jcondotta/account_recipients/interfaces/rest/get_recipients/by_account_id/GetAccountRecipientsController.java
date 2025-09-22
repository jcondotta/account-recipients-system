package com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id;

import com.jcondotta.account_recipients.interfaces.rest.get_recipients.by_account_id.model.GetAccountRecipientsResponse;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.account-recipients.root-path}")
public interface GetAccountRecipientsController {

    @Timed(
        value = "account-recipient.get-by-account-id.time",
        description = "account recipient creation time measurement",
        percentiles = {0.5, 0.95, 0.99}
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetAccountRecipientsResponse> byAccountId(@PathVariable("bank-account-id") UUID bankAccountId);
}