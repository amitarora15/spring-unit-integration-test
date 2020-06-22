package com.amit.springtest.repository;

import com.amit.springtest.entity.Content;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepositoryUnderTest;

    @Test
    @Sql("classpath:createContent.sql")
    public void givenYearWhenFindAllByReleaseAfterThenReturnContent(){
        Optional<List<Content>> optionalContents = contentRepositoryUnderTest.findAllByYearOfReleaseAfter(2004L);
        assertThat(optionalContents).isNotEmpty();
    }

    @Test
    @Sql("classpath:createContent.sql")
    public void givenYearWhenFindAllByReleaseAfterThenReturnValidateContent(){
        Optional<List<Content>> optionalContents = contentRepositoryUnderTest.findAllByYearOfReleaseAfter(2004L);
        List<Content> expectedResponse = new ArrayList<>();
        expectedResponse.add(Content.builder().title("MI-2").id(2L).yearOfRelease(2005L).build());
        expectedResponse.add(Content.builder().title("MI-3").id(3L).yearOfRelease(2010L).build());
        assertIterableEquals(expectedResponse, optionalContents.get());
    }

}
