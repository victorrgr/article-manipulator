package com.web.scraper.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.scraper.data.entity.Article;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Utils {
    private static final Log LOGGER = LogFactory.getLog(Utils.class);
    public static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    public static ObjectMapper mapper;
    private static Environment env;

    public Utils(Environment environment, ObjectMapper objectMapper) {
        Utils.env = environment;
        Utils.mapper = objectMapper;
    }

    public static String getProperty(String property) {
        return env.getProperty(property);
    }

    public static String toString(Class<? extends Article> aClass, Object data) {
        if (aClass == null || data == null)
            return "[ERROR IN TO_STRING]";
        try {
            return mapper.writeValueAsString(Map.of(StringUtils.uncapitalize(aClass.getSimpleName()), data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
