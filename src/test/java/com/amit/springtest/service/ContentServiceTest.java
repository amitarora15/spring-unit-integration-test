package com.amit.springtest.service;

import com.amit.springtest.bean.ContentVo;
import com.amit.springtest.entity.Content;
import com.amit.springtest.repository.ContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentServiceUnderTest;

    @Test
    public void whenGetContentsThenAllContents(){

        List<Content> contentList = Arrays.asList(Content.builder().id(1L).title("MI-1").yearOfRelease(2004L).build(),new Content(1L, "MI-2", null, 2005L));
        List<ContentVo> expectedContent = Arrays.asList(ContentVo.builder().id(1L).title("MI-1").yearOfRelease(2004L).build(),new ContentVo(1L, "MI-2", null, 2005L));

        given(contentRepository.findAll()).willReturn(contentList);

        List<ContentVo> contentVoList = contentServiceUnderTest.getContents();
        assertIterableEquals(expectedContent, contentVoList);

        then(contentRepository).should().findAll();
        then(contentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    public void givenYearWhenGetLatestContentsThenAllContents(){

        Optional<List<Content>> contentList = Optional.ofNullable(Arrays.asList(new Content(1L, "MI-2", null, 2005L)));
        List<ContentVo> expectedContent = Arrays.asList(new ContentVo(1L, "MI-2", null, 2005L));

        given(contentRepository.findAllByYearOfReleaseAfter(anyLong())).willReturn(contentList);

        List<ContentVo> contentVoList = contentServiceUnderTest.getLatestContents(2004L);
        assertIterableEquals(expectedContent, contentVoList);

        then(contentRepository).should().findAllByYearOfReleaseAfter(anyLong());
        then(contentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    public void givenYearWhenGetLatestContentsThenNull(){

        given(contentRepository.findAllByYearOfReleaseAfter(anyLong())).willReturn(Optional.ofNullable(null));

        List<ContentVo> contentVoList = contentServiceUnderTest.getLatestContents(2004L);
        assertThat(contentVoList).isNull();

        then(contentRepository).should().findAllByYearOfReleaseAfter(anyLong());
        then(contentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    public void givenIdWhenGetContentThenContent(){
        Optional<Content> content = Optional.of(Content.builder().id(1L).title("MI-1").yearOfRelease(2004L).build());
        ContentVo expectedVo = ContentVo.builder().id(1L).title("MI-1").yearOfRelease(2004L).build();
        given(contentRepository.findById(content.get().getId())).willReturn(content);

        ContentVo vo = contentServiceUnderTest.getContent(1L);
        assertThat(vo).isEqualToComparingFieldByField(expectedVo);

        then(contentRepository).should().findById(ArgumentMatchers.eq(1L));
        then(contentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    public void givenInvalidIdWhenGetContentThenNull(){
        given(contentRepository.findById(-1L)).willReturn(Optional.ofNullable(null));

        ContentVo vo = contentServiceUnderTest.getContent(-1L);
        assertThat(vo).isNull();

        then(contentRepository).should().findById(ArgumentMatchers.eq(-1L));
        then(contentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    public void givenContentWhenAddContentThenContentAdded(){
        Content content = Content.builder().id(1L).title("MI-1").yearOfRelease(2004L).build();
        ContentVo inputVo = ContentVo.builder().title("MI-1").yearOfRelease(2004L).build();
        ContentVo expectedVo = ContentVo.builder().id(1L).title("MI-1").yearOfRelease(2004L).build();
        given(contentRepository.save(isA(Content.class))).willReturn(content);

        ContentVo vo = contentServiceUnderTest.addContent(inputVo);
        assertThat(vo).isEqualToComparingFieldByField(expectedVo);

        ArgumentCaptor<Content> captor = ArgumentCaptor.forClass(Content.class);
        then(contentRepository).should().save(captor.capture());
        Content capturedContent = captor.getValue();
        assertThat(capturedContent).isEqualToComparingFieldByField(inputVo);

        then(contentRepository).shouldHaveNoMoreInteractions();

    }

}
