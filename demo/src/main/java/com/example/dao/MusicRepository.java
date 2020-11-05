package com.example.dao;

import com.example.EntityClass.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music,Long> {
    Music findByName(String name);

    @Query("select m from Music m where m.name like ?1")
    List<Music> findsByName(String name);

    @Query("select count(m.id) from Music m")
    Long findCount();

    @Query("select max(m.id) from Music m")
    Long findMaxId();
}
