package com.example.service.imp;

import com.example.EntityClass.Record;
import com.example.Exception.NoFonudException;
import com.example.dao.RecordRepository;
import com.example.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public Page<Record> getAll(Pageable pageable) {
        return recordRepository.findAll(pageable);
    }

    @Override
    public Record getRecordById(Long id) {
        try{
            return recordRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e){
            return null;
        }
    }

    @Override
    public Record saveRecord(Record record) {
        return recordRepository.save(record);
    }

    @Override
    public Record updateRecordById(Long id, Record record) {
        Record r = getRecordById(id);
        if(r == null){
            throw new NoFonudException("不存在该type数据");
        }
        BeanUtils.copyProperties(record,r);
        return saveRecord(r);
    }

    @Override
    public Long getMaxIdCount() {
        try {
            Long count = recordRepository.findMaxCount();
            return count;
        }catch (org.springframework.orm.jpa.JpaSystemException e){
            return  null;
        }

    }

    @Override
    public List<Record> getRecordsByClassMethodAndCount(String ClassMethod, Long count) {
        return recordRepository.findAllByClassMethodAndCount(ClassMethod,count);
    }

    @Override
    public List<Record> getRecordsByUrlAndCount(String url, Long count) {
        return recordRepository.findAllByUrlAndCount(url,count);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            recordRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
