package com.jcondotta.account_recipients.application.common.fixtures;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountRecipientFixtures {

    JEFFERSON("Jefferson Condotta", "ES3801283316232166447417"),
    PATRIZIO("Patrizio Condotta", "IT93Q0300203280175171887193"),
    VIRGINIO("Virginio Condotta", "GB82WEST12345698765432");

    private final String recipientName;
    private final String iban;

}