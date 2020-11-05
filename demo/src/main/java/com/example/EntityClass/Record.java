package com.example.EntityClass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Record {

    @Id
    @GeneratedValue
    private Long id;
    private Long count;

    private String name;
    private String url;
    private String ip;
    private String classMethod;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createTime;

    public Record(){};

    public Record(String name, String url, String ip, String classMethod) {
        this.name = name;
        this.url = url;
        this.ip = ip;
        this.classMethod = classMethod;
        count = 0L;
    }

    public Record(Long count, String name, String url, String ip, String classMethod) {
        this.count = count;
        this.name = name;
        this.url = url;
        this.ip = ip;
        this.classMethod = classMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", count=" + count +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", ip='" + ip + '\'' +
                ", ClassMethod='" + classMethod + '\'' +
                '}';
    }
}
