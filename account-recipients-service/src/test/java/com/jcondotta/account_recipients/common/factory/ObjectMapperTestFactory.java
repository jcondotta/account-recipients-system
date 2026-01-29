package com.jcondotta.account_recipients.common.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public class ObjectMapperTestFactory {

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule());
  }
}
