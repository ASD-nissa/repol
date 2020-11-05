package com.example.utils;

import com.example.EntityClass.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewObject {
    public static Blog CreateNewBlog(Blog blog){
        Blog nBlog= new Blog();
        //设置blog user
        User nUser = new User();
        nUser.setImg(blog.getUser().getImg());
        nUser.setNickname(blog.getUser().getNickname());
        nBlog.setUser(nUser);
        //blog 时间
        nBlog.setUpdateTime(blog.getUpdateTime());
        //blog 首图
        nBlog.setFristPicture(blog.getFristPicture());
        //blog 标识 原创or..
        nBlog.setFlag(blog.getFlag());
        //blog title
        nBlog.setTitle(blog.getTitle());
        //blog 内容
        nBlog.setContent(blog.getContent());
        //blog 标签
        List<Tag> nTags = new ArrayList<>();
        for(Tag old : blog.getTags()){
            Tag nTag = new Tag();
            nTag.setId(old.getId());
            nTag.setName(old.getName());
            nTags.add(nTag);
        }
        nBlog.setTags(nTags);
        //Blog 赞赏
        nBlog.setAppreciate(blog.getAppreciate());
        //blog 评论功能
        nBlog.setCommentabled(blog.getCommentabled());
        //blog tagsId
        nBlog.setTagIds(blog.getTagIds());
        //blog 的图片保存的文件夹
        nBlog.setImgName(blog.getImgName());

        nBlog.setRecommend(blog.getRecommend());
        nBlog.setStatement(blog.getStatement());
        nBlog.setAuthorDetails(blog.getAuthorDetails());
        //type
        Type nType = new Type();
        nType.setId(blog.getType().getId());
        nType.setName(blog.getType().getName());
        nBlog.setType(nType);
        //nBlog 完成

        return nBlog;
    }

    //用于列表展示不具有blog的内容
    public static List<Blog> CreateNewBlogLit(List<Blog> blogs){
        List<Blog> blogList = new ArrayList<>();
        for (Blog oBlog : blogs) {
            Blog nBlog = new Blog();
            nBlog.setTitle(oBlog.getTitle());
            nBlog.setAuthorDetails(oBlog.getAuthorDetails());
            //user
            User nUser = new User();
            nUser.setImg(oBlog.getUser().getImg());
            nUser.setNickname(oBlog.getUser().getNickname());
            nUser.setUsername(oBlog.getUser().getUsername());
            nBlog.setUser(nUser);
            //
            nBlog.setUpdateTime(oBlog.getUpdateTime());
            nBlog.setViews(oBlog.getViews());
            //type
            Type nType = new Type();
            nType.setId(oBlog.getType().getId());
            nType.setName(oBlog.getType().getName());
            nBlog.setType(nType);
            //tags
            List<Tag> tagList = new ArrayList<>();
            for(Tag oTag : oBlog.getTags()){
                Tag nTag = new Tag();
                nTag.setName(oTag.getName()+"._?"+oTag.getBlogs().size());
                nTag.setId(oTag.getId());
                tagList.add(nTag);
            }
            nBlog.setTags(tagList);
            //
            nBlog.setId(oBlog.getId());
            nBlog.setFristPicture(oBlog.getFristPicture());
            //发布
            nBlog.setState(oBlog.getState());
            //推荐
            nBlog.setRecommend(oBlog.getRecommend());
            //标签
            nBlog.setFlag(oBlog.getFlag());
            blogList.add(nBlog);
        }

        return blogList;
    }

    public static List<Comment> CreateNewCommentList(List<Comment> comments){
        List<Comment> commentList = new ArrayList<>();
        for(Comment old : comments){
            for(Comment oldSon : old.getSonComments()){
                oldSon.setSonComments(null);
                oldSon.setBlog(null);
                oldSon.setParentComment(null);
            }
            Comment nComment = new Comment();
            nComment.setImg(old.getImg());
            nComment.setNickname(old.getNickname());
            nComment.setCreateTime(old.getCreateTime());
            nComment.setContent(old.getContent());
            nComment.setId(old.getId());
            nComment.setSonComments(old.getSonComments());
            commentList.add(nComment);
        }

        return commentList;
    }

    public static List<Map> CreateNewTypeList(List<Type> types){
        List<Map> typeList = new ArrayList<>();
        for(Type old : types){
            Map nType = new HashMap();
            Map nUser = new HashMap();
            nType.put("id",old.getId());
            nType.put("name",old.getName());
            nType.put("size",old.getBlogs().size());

            nUser.put("username",old.getUser().getUsername());
            nType.put("user",nUser);
            typeList.add(nType);
        }
        return  typeList;
    }

    public static List<Map> CreateNewTagList(List<Tag> tags){
        List<Map> tagList = new ArrayList<>();
        for(Tag old : tags){
            Map nTag = new HashMap();
            Map nUser = new HashMap();
            nTag.put("id",old.getId());
            nTag.put("name",old.getName());
            nTag.put("size",old.getBlogs().size());

            nUser.put("username",old.getUser().getUsername());
            nTag.put("user",nUser);
            tagList.add(nTag);
        }

        return tagList;
    }

    public static List<User> CreateNewUserList(List<User> users){
        List<User> userList = new ArrayList();
        for(User oUser : users){
            User nUser = new User();
            nUser.setId(oUser.getId());
            nUser.setNickname(oUser.getNickname());
            nUser.setUsername(oUser.getUsername());
            nUser.setType(oUser.getType());
            nUser.setCreateTime(oUser.getCreateTime());
            userList.add(nUser);
        }

        return userList;
    }
}
