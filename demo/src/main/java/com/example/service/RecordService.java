package com.example.service;

import com.example.EntityClass.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecordService {

    Page<Record> getAll(Pageable pageable);

    List<Record> getRecordsByClassMethodAndCount(String ClassMethod, Long count);

    List<Record> getRecordsByUrlAndCount(String url,Long count);

    Record saveRecord(Record record);

    Record updateRecordById(Long id,Record record);

    Record getRecordById(Long id);

    Long getMaxIdCount();

    boolean deleteById(Long id);
}
