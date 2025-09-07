package com.jcondotta.account_recipients.domain.recipient.value_objects;

public record RecipientName(String value) {

    public static RecipientName of(String value) {
        return new RecipientName(value);
    }
}