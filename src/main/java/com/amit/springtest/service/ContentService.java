package com.amit.springtest.service;

import com.amit.springtest.bean.ContentVo;
import com.amit.springtest.entity.Content;
import com.amit.springtest.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    private ContentVo getVo(Content entity) {
        return ContentVo.builder().id(entity.getId()).description(entity.getDescription()).title(entity.getTitle()).yearOfRelease(entity.getYearOfRelease()).build();
    }

    private Content getEntity(ContentVo vo){
        return Content.builder().description(vo.getDescription()).id(vo.getId()).title(vo.getTitle()).yearOfRelease(vo.getYearOfRelease()).build();
    }

    public List<ContentVo> getContents() {
        Iterable<Content> contents = contentRepository.findAll();
        List<ContentVo> vos = new ArrayList<>();
        contents.forEach(c -> vos.add(getVo(c)));
        return vos;
    }

    public List<ContentVo> getLatestContents(Long releaseYear){
        Optional<List<Content>> contents = contentRepository.findAllByYearOfReleaseAfter(releaseYear);
        if(contents.isPresent()){
            return contents.get().stream().map(content -> getVo(content)).collect(Collectors.toList());
        }
        return null;
    }

    public ContentVo getContent(Long id){
        Optional<Content> content = contentRepository.findById(id);
        if(content.isPresent())
            return getVo(content.get());
        return null;
    }

    public ContentVo addContent(ContentVo vo){
        Content entity = getEntity(vo);
        entity = contentRepository.save(entity);
        return getVo(entity);
    }

    public void updateContent(Long id, ContentVo vo){
        Optional<Content> content = contentRepository.findById(id);
        if(content.isPresent()){
            Content entity = getEntity(vo);
            contentRepository.save(entity);
        } else {
            throw new IllegalArgumentException("Content not present");
        }
    }

    public void deleteContent(Long id) {
        Optional<Content> content = contentRepository.findById(id);
        if(content.isPresent()){
            contentRepository.delete(content.get());
        } else {
            throw new IllegalArgumentException("Content not present");
        }
    }

}
