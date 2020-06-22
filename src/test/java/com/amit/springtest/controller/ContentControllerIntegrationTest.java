package com.amit.springtest.controller;

import com.amit.springtest.bean.ErrorVo;
import com.amit.springtest.entity.Content;
import com.amit.springtest.repository.ContentRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
        @Sql(value = "classpath:createContent.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:deleteContent.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class ContentControllerIntegrationTest {

    @LocalServerPort
    private Long port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ContentRepository contentRepository;

    @Test
    @Tag("integration-test")
    public void givenContentWhenUpdatedThenUpdated() throws Exception {

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/api/contents/{id}", HttpMethod.DELETE, null, Void.class, 3L);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Optional<Content> optionalContent = contentRepository.findById(3L);
        assertThat(optionalContent.isPresent()).isFalse();
    }

    @Test
    @Tag("integration-test")
    public void givenInvalidContentWhenUpdatedThenException() throws Exception {

        List<ErrorVo> expectedErrorVo = Arrays.asList(ErrorVo.builder().exceptionName(IllegalArgumentException.class.getCanonicalName()).message("Content not present").build());
        ResponseEntity<List<ErrorVo>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/api/contents/{id}", HttpMethod.DELETE, null, new ParameterizedTypeReference<List<ErrorVo>>() {}, 10L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).hasSizeGreaterThanOrEqualTo(1);
        assertIterableEquals(expectedErrorVo, responseEntity.getBody());

        Optional<Content> optionalContent = contentRepository.findById(10L);
        assertThat(optionalContent.isPresent()).isFalse();
    }

}
