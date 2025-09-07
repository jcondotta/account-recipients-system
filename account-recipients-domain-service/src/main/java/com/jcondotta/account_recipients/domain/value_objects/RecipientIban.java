package com.jcondotta.account_recipients.domain.value_objects;

public record RecipientIban(String value) {

    public static RecipientIban of(String value) {
        return new RecipientIban(value);
    }
}