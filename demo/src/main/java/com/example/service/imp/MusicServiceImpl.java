package com.example.service.imp;

import com.example.EntityClass.Music;
import com.example.dao.MusicRepository;
import com.example.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicRepository musicRepository;

    @Override
    public Page<Music> getAll(Pageable pageable) {
        return musicRepository.findAll(pageable);
    }

    @Override
    public Music getOne(String name) {
        return musicRepository.findByName(name);
    }

    @Override
    public Music getOne(Long id) {
        try{
            return musicRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e){
            return null;
        }
    }
    

    @Override
    public Long getCount() {
        return musicRepository.findCount();
    }

    @Override
    public Long getMaxId() {
        return musicRepository.findMaxId();
    }

    @Override
    public List<Music> listByName(String name) {
        return musicRepository.findsByName("%"+name+"%");
    }

    @Override
    public List<Music> getAll() {
        return musicRepository.findAll();
    }

    @Override
    public Music save(Music music) {
        return musicRepository.save(music);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Music music = getOne(id);
        if(music != null)
            musicRepository.delete(music);
    }

    @Transactional
    @Override
    public void delete(String name) {
        Music music = getOne(name);
        if(music != null)
            musicRepository.delete(music);
    }
}
