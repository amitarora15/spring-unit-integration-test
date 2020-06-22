package com.amit.springtest.controller;

import com.amit.springtest.bean.ContentVo;
import com.amit.springtest.service.ContentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static com.amit.springtest.controller.ResponseBodyMatchers.responseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContentController.class)
@ActiveProfiles("test")
public class ContentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentService contentService;

    private List<ContentVo> actualContents = new ArrayList<>();

    private ContentVo actualContent;

    @BeforeEach
    public void initialize() {
        actualContent = ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build();
        actualContents.add(actualContent);
    }

    @Test
    public void givenWhenGetContentsThenContents() {

        List<ContentVo> expectedContentVos = new ArrayList<>();
        expectedContentVos.add(ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build());

        try {
            when(contentService.getContents()).thenReturn(actualContents);

            MvcResult result = mockMvc.perform(get("/api/contents").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

            verify(contentService, times(1)).getContents();
            verify(contentService, never()).getLatestContents(anyLong());

            String actualOutput = result.getResponse().getContentAsString();
            assertThat(actualOutput).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expectedContentVos));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void givenInvalidUrlWhenGetContentsThen404() {

        try {
            mockMvc.perform(get("/api/content").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void givenWhenGetContentsThenContentsUsingMatchers() {

        List<ContentVo> expectedContentVos = new ArrayList<>();
        expectedContentVos.add(ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build());
        try {
            when(contentService.getContents()).thenReturn(actualContents);

            mockMvc.perform(get("/api/contents").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(responseBody().containsListOfObjectAsJson(expectedContentVos, ContentVo.class));

            verify(contentService, times(1)).getContents();
            verify(contentService, never()).getLatestContents(anyLong());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenYearOfReleaseWhenGetContentsThenContentsUsingMatchers() {

        List<ContentVo> expectedContentVos = new ArrayList<>();
        expectedContentVos.add(ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build());
        try {
            when(contentService.getLatestContents(anyLong())).thenReturn(actualContents);

            mockMvc.perform(get("/api/contents").param("yearOfRelease", "2003").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(responseBody().containsListOfObjectAsJson(expectedContentVos, ContentVo.class));

            verify(contentService, times(1)).getLatestContents(ArgumentMatchers.eq(2003L));
            verify(contentService, never()).getContents();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenIdWhenGetContentThenContentUsingMatchers() {

        ContentVo expectedContentVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build();
        try {
            when(contentService.getContent(anyLong())).thenReturn(actualContent);

            mockMvc.perform(get("/api/contents/{id}", anyLong()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(responseBody().containsObjectAsJson(expectedContentVo, ContentVo.class));

            verify(contentService, times(1)).getContent(anyLong());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenContentWhenAddContentThenContentAdded() {

        ContentVo expectedContentVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).id(1L).build();
        ContentVo inputVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).build();
        try {
            when(contentService.addContent(isA(ContentVo.class))).thenReturn(actualContent);

            mockMvc.perform(post("/api/contents").content(objectMapper.writeValueAsString(inputVo)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(responseBody().containsObjectAsJson(expectedContentVo, ContentVo.class));

            ArgumentCaptor<ContentVo> captor = ArgumentCaptor.forClass(ContentVo.class);
            verify(contentService, times(1)).addContent(captor.capture());
            ContentVo passedInput = captor.getValue();
            assertThat(passedInput).isEqualToComparingFieldByField(inputVo);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenInvalidContentWhenAddContentThenFailed() {

        ContentVo inputVo = ContentVo.builder().title(null).yearOfRelease(2004L).build();
        try {
            mockMvc.perform(post("/api/contents").content(objectMapper.writeValueAsString(inputVo)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(responseBody().containsListOfErrorsAsJson(MethodArgumentNotValidException.class.getCanonicalName(), "title"));

            verify(contentService, never()).addContent(isA(ContentVo.class));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenContentWhenUpdateContentThenContentUpdated() {

        ContentVo inputVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).build();
        try {
            doNothing().when(contentService).updateContent(anyLong(), isA(ContentVo.class));

            mockMvc.perform(put("/api/contents/{id}", 1L).content(objectMapper.writeValueAsString(inputVo)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

            ArgumentCaptor<ContentVo> captor = ArgumentCaptor.forClass(ContentVo.class);
            verify(contentService, times(1)).updateContent(ArgumentMatchers.eq(1L), captor.capture());
            ContentVo passedInput = captor.getValue();
            assertThat(passedInput).isEqualToComparingFieldByField(inputVo);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenInvalidContentWhenUpdateContentThenFailed() {

        ContentVo inputVo = ContentVo.builder().title(null).yearOfRelease(2004L).build();
        try {
            mockMvc.perform(put("/api/contents/{id}", 1L).content(objectMapper.writeValueAsString(inputVo)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(responseBody().containsListOfErrorsAsJson(MethodArgumentNotValidException.class.getCanonicalName(), "title"));

            verify(contentService, never()).updateContent(anyLong(), isA(ContentVo.class));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenInvalidIdWhenUpdateContentThenFailed() {
        ContentVo inputVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).build();
        try {
            doThrow(new IllegalArgumentException()).when(contentService).updateContent(anyLong(), isA(ContentVo.class));
            mockMvc.perform(put("/api/contents/{id}", 1L).content(objectMapper.writeValueAsString(inputVo)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(responseBody().containsListOfErrorsAsJson(IllegalArgumentException.class.getCanonicalName()));

            ArgumentCaptor<ContentVo> captor = ArgumentCaptor.forClass(ContentVo.class);
            verify(contentService, times(1)).updateContent(ArgumentMatchers.eq(1L), captor.capture());
            ContentVo passedInput = captor.getValue();
            assertThat(passedInput).isEqualToComparingFieldByField(inputVo);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void givenIdWhenDeleteContentThenContentDeleted() {

        try {
            mockMvc.perform(delete("/api/contents/{id}", 1L).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

            verify(contentService, times(1)).deleteContent(ArgumentMatchers.eq(1L));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givenIdWhenDeleteContentThenFailed() {

        try {
            doThrow(new IllegalArgumentException()).when(contentService).deleteContent(anyLong());
            mockMvc.perform(delete("/api/contents/{id}", 1L).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(responseBody().containsListOfErrorsAsJson(IllegalArgumentException.class.getCanonicalName()));

            verify(contentService, times(1)).deleteContent(ArgumentMatchers.eq(1L));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


}
