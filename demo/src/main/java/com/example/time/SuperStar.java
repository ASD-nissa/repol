package com.example.time;

import com.example.EntityClass.superstar.Question;
import com.example.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
@Slf4j
@Async
public class SuperStar {

    Logger TimeLog = LoggerFactory.getLogger(SuperStar.class);
    @Autowired
    QuestionService questionService;

    @Scheduled(cron="${my.superStarWeightRemovalTime}")
    public void WeightRemoval(){
        List<Question> questions = questionService.getAll();
        Map<String,Question> retain = new HashMap<>();
        List<Question> removeList = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i).getQuestion();
            String da = questions.get(i).getDa();
            String key = question+da;
            if(retain.get(key)==null){
                retain.put(key,questions.get(i));
            }else{
                removeList.add(questions.get(i));
            }
        }

        TimeLog.info("time:"+new Date()+" removeSize:"+removeList.size());
        for (int i = 0; i < removeList.size(); i++) {
            questionService.delete(removeList.get(i));
        }
    }
}
