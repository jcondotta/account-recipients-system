package com.jcondotta.recipients.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "api.v1.recipients")
public record RecipientURIProperties(
    @NotBlank String rootPath, @NotBlank String recipientIdPath) {

  public URI recipientsURI(UUID bankAccountId) {
    var expanded = rootPath.replace("{bank-account-id}", bankAccountId.toString());
    return URI.create(expanded);
  }

  public URI recipientURI(UUID bankAccountId, UUID recipientId) {
    String expanded = recipientIdPath.
        replace("{bank-account-id}", bankAccountId.toString())
        .replace("{recipient-id}", recipientId.toString());
    return URI.create(expanded);
  }
}
