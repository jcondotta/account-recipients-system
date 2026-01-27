package com.jcondotta.account_recipients.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "api.v1.account-recipients")
public record AccountRecipientURIProperties(
    @NotBlank String rootPath, @NotBlank String recipientIdPath) {

  public URI accountRecipientsURI(UUID bankAccountId) {
    var expanded = rootPath.replace("{bank-account-id}", bankAccountId.toString());
    return URI.create(expanded);
  }

  public URI accountRecipientURI(UUID bankAccountId, UUID accountRecipientId) {
    String expanded = recipientIdPath.
        replace("{bank-account-id}", bankAccountId.toString())
        .replace("{recipient-id}", accountRecipientId.toString());
    return URI.create(expanded);
  }
}
