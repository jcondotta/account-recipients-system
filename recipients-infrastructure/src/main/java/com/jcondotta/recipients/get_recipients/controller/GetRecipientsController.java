package com.jcondotta.recipients.get_recipients.controller;

import com.jcondotta.recipients.get_recipients.controller.model.request.GetRecipientsRestRequestParams;
import com.jcondotta.recipients.get_recipients.controller.model.response.GetRecipientsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("${api.v1.recipients.root-path}")
public interface GetRecipientsController {

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<GetRecipientsResponse> byQuery(
      @PathVariable("bank-account-id") UUID bankAccountId,
      @ModelAttribute GetRecipientsRestRequestParams restRequestParams);
}
