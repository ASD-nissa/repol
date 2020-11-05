package com.example.dao;

import com.example.EntityClass.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record,Long> {

    @Query("select max(r.count) from Record r")
    Long findMaxCount();

    @Query("select r from Record r where r.classMethod = ?1 and r.count = ?2")
    List<Record> findAllByClassMethodAndCount(String ClassMethod, Long count);

    List<Record> findAllByUrlAndCount(String url,Long count);
}
