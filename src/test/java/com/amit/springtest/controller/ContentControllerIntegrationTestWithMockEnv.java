package com.amit.springtest.controller;

import com.amit.springtest.bean.ContentVo;
import com.amit.springtest.entity.Content;
import com.amit.springtest.repository.ContentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.amit.springtest.controller.ResponseBodyMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(value = "classpath:createContent.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:deleteContent.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class ContentControllerIntegrationTestWithMockEnv {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContentRepository contentRepository;

    @Test
    public void givenContentWhenUpdatedThenUpdated() throws Exception {
        ContentVo vo = new ContentVo(3L, "MI-4", "Test", 2010L);
        mockMvc.perform(put("/api/contents/{id}", 3L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(vo))).andExpect(status().isNoContent());

        Optional<Content> optionalContent = contentRepository.findById(3L);
        assertThat(optionalContent.isPresent()).isTrue();
        assertEquals(vo.getTitle(), optionalContent.get().getTitle());
        assertEquals(vo.getDescription(), optionalContent.get().getDescription());
    }

    @Test
    public void givenInvalidContentWhenUpdatedThenException() throws Exception {
        ContentVo vo = new ContentVo(10L, "MI-4", "Test", 2010L);
        mockMvc.perform(put("/api/contents/{id}", 10L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(vo))).andExpect(status().isBadRequest()).andExpect(responseBody().containsListOfErrorsAsJson(IllegalArgumentException.class.getCanonicalName()));

        Optional<Content> optionalContent = contentRepository.findById(10L);
        assertThat(optionalContent.isPresent()).isFalse();
    }

}
