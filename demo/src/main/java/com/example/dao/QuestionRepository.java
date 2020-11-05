package com.example.dao;

import com.example.EntityClass.superstar.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    Question findByQuestion(String question);

    Question findByQuestionAndDa(String question,String da);

    List<Question> findAllByQuestionAndDa(String question,String da);

    @Query("select q from Question q where q.question like ?1")
    List<Question>findAllByQuestionLike(String question);
}
