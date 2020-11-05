package com.example.service;

import com.example.EntityClass.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MusicService {
    Page<Music> getAll(Pageable pageable);

    Music getOne(String name);

    Music getOne(Long id);

    Long getCount();

    Long getMaxId();

    List<Music> listByName(String name);

    List<Music> getAll();

    Music save(Music music);

    void delete(Long id);

    void delete(String name);
}
