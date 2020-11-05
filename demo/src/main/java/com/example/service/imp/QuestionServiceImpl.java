package com.example.service.imp;

import com.example.EntityClass.superstar.Question;
import com.example.dao.QuestionRepository;
import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    @Override
    public Question getOne(Long id) {
        try {
            return questionRepository.findById(id).get();
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public Question getOne(String question) {
        return questionRepository.findByQuestion(question);
    }

    @Override
    public Question getOne(String question, String da) {
        return questionRepository.findByQuestionAndDa(question,da);
    }

    @Override
    public List<Question> getList(String question, String da) {
        return questionRepository.findAllByQuestionAndDa(question,da);
    }

    @Override
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> likeList(String question) {
        return questionRepository.findAllByQuestionLike("%"+question+"%");
    }

    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    @Override
    public void delete(Question question) {
        Question q = getOne(question.getId());
        if(q != null)
            questionRepository.delete(q);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Question q = getOne(id);
        if(q != null) {
            questionRepository.delete(q);
        }
    }
}
