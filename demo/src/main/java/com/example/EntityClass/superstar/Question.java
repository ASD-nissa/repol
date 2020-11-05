package com.example.EntityClass.superstar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Question {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String question;
    @Lob
    private String da;

    public Question(){};

    public Question(String question,String da){
        this.question = question;
        this.da = da;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDa() {
        return da;
    }

    public void setDa(String da) {
        this.da = da;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", da='" + da + '\'' +
                '}';
    }
}
