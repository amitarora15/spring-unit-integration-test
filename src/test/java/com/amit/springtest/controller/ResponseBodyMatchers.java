package com.amit.springtest.controller;

import com.amit.springtest.bean.ErrorVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.assertj.core.api.Assertions.*;

public class ResponseBodyMatchers {

    private ObjectMapper objectMapper = new ObjectMapper();

    public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String actualOutputString = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(actualOutputString, targetClass);
            assertThat(actualObject).isEqualToComparingFieldByField(expectedObject);
        };
    }

    public <T> ResultMatcher containsListOfObjectAsJson(List<T> expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String actualOutputString = mvcResult.getResponse().getContentAsString();
            List<T> actualObject = objectMapper.readValue(actualOutputString, objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
            assertIterableEquals(expectedObject, actualObject);
        };
    }

    public ResultMatcher containsListOfErrorsAsJson(String exceptionName) {
        return mvcResult -> {
            String actualOutputString = mvcResult.getResponse().getContentAsString();
            List<ErrorVo> actualObject = objectMapper.readValue(actualOutputString, objectMapper.getTypeFactory().constructCollectionType(List.class, ErrorVo.class));
            List<ErrorVo> filteredErrorVo = actualObject.stream().filter(a -> a.getExceptionName().equals(exceptionName)).collect(Collectors.toList());
            assertThat(filteredErrorVo).hasSizeGreaterThanOrEqualTo(1).withFailMessage("Error with %s exception name", exceptionName);
        };
    }

    public ResultMatcher containsListOfErrorsAsJson(String exceptionName, String fieldName) {
        return mvcResult -> {
            String actualOutputString = mvcResult.getResponse().getContentAsString();
            List<ErrorVo> actualObject = objectMapper.readValue(actualOutputString, objectMapper.getTypeFactory().constructCollectionType(List.class, ErrorVo.class));
            List<ErrorVo> filteredErrorVo = actualObject.stream().filter(a -> a.getExceptionName().equals(exceptionName)).filter(a -> a.getFieldName().equals(fieldName)).collect(Collectors.toList());
            assertThat(filteredErrorVo).hasSizeGreaterThanOrEqualTo(1).withFailMessage("Error with %s exception name and field name %s", exceptionName, fieldName);
        };
    }

    static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }

}
