package com.example.service;

import com.example.EntityClass.superstar.Question;

import java.util.List;

public interface QuestionService {

    Question getOne(Long id);

    Question getOne(String question);

    Question getOne(String question,String da);

    List<Question> getList(String question,String da);

    List<Question> likeList(String question);

    List<Question> getAll();

    Question save(Question question);

    void delete(Question question);

    void delete(Long id);
}
