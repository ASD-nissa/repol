package com.example.vo;

import java.util.List;

public class semanticJson<T> {

    private boolean success;
    private List<T> results;

    public semanticJson() {
    }

    public semanticJson(boolean t,List<T> results){
        this.results = results;
        this.success = t;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
