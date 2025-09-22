package com.jcondotta.account_recipients;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class ClockTestFactory {

    public static final Clock testClockFixed = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
}