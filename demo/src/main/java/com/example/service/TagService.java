package com.example.service;

import com.example.EntityClass.Tag;
import com.example.EntityClass.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Tag saveTag(Tag tag);

    Tag getTagById(Long id);

    List<Tag> getAll(User user);

    List<Tag> getAll();

    List<Tag> ListByIds(String ids);

    Tag getTagByName(String name);

    Page<Tag> listTags(Pageable pageable,User user);

    List<Tag> listTop(Integer size);

    Tag updateTag(Tag tag);

    boolean delete(Long id);
}
