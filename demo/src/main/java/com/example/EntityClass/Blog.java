package com.example.EntityClass;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="t_blog")
@EntityListeners(AuditingEntityListener.class)
public class Blog {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String imgName;
    @Lob
    private String fristPicture;

    @Transient//不会保存到数据库
    private String tagIds;

    private String flag;
    //内容
    @NotBlank(message = "正文不能为空")
    @Lob
    private String content;
    @Lob
    private String authorDetails;
    private Integer views;
    private Boolean appreciate;
    private Boolean statement;
    private Boolean commentabled;
    //发布状态
    private Boolean state;
    private Boolean recommend;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne
    private Type type;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<>();

    public Blog() {}

    public void initBlog(){
        appreciate = false;
        statement = false;
        commentabled = false;
        state = false;
        recommend = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getFristPicture() {
        return fristPicture;
    }

    public void setFristPicture(String fristPicture) {
        this.fristPicture = fristPicture;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorDetails() {
        return authorDetails;
    }

    public void setAuthorDetails(String authorDetails) {
        this.authorDetails = authorDetails;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Boolean getAppreciate() {
        return appreciate;
    }

    public void setAppreciate(Boolean appreciate) {
        this.appreciate = appreciate;
    }

    public Boolean getStatement() {
        return statement;
    }

    public void setStatement(Boolean statement) {
        this.statement = statement;
    }

    public Boolean getCommentabled() {
        return commentabled;
    }

    public void setCommentabled(Boolean commentabled) {
        this.commentabled = commentabled;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imgName='" + imgName + '\'' +
                ", fristPicture='" + fristPicture + '\'' +
                ", flag='" + flag + '\'' +
                ", content='" + content + '\'' +
                ", views=" + views +
                ", appreciate=" + appreciate +
                ", statement=" + statement +
                ", commentabled=" + commentabled +
                ", state=" + state +
                ", recommend=" + recommend +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", type=" + type +
                ", tags=" + tags +
                ", user=" + user +
                ", comments=" + comments +
                '}';
    }
}
