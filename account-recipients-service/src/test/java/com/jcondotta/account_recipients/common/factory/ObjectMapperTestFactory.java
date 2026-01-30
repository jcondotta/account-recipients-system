package com.jcondotta.account_recipients.common.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperTestFactory {

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule());
  }
}
