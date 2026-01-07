package com.jcondotta.account_recipients.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "api.v1.account-recipients")
public record AccountRecipientURIProperties(
    @NotBlank String rootPath, @NotBlank String accountRecipientIdPath) {

  public URI accountRecipientsURI(UUID bankAccountId) {
    var expanded = rootPath.replace("{bank-account-id}", bankAccountId.toString());
    return URI.create(expanded);
  }
}
