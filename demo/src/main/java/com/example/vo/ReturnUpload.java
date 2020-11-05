package com.example.vo;

public class ReturnUpload {
    private String url;
    private String message;
    private Integer success;

    public ReturnUpload(String url,String message,int success) {
        this.url = url;
        this.message = message;
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }
}
