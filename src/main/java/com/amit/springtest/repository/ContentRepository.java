package com.amit.springtest.repository;

import com.amit.springtest.entity.Content;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends CrudRepository<Content, Long> {

    @Query(value = "select * from content where year_of_release > :year", nativeQuery = true)
    Optional<List<Content>> findAllByYearOfReleaseAfter(@Param("year") Long year);


}
