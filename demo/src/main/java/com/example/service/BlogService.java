package com.example.service;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Tag;
import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import com.example.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {

    Blog getBolgById(Long id);

    Blog getBolgByTitle(String title);

    Page<Blog> listBlogsByType(Pageable pageable,Type type);

    Page<Blog> listBlogsByTagId(Long id,Pageable pageable);

    Page<Blog> listBlogs(Pageable pageable, BlogQuery blog, User user);

    Page<Blog> listBlogs(Pageable pageable);

    Page<Blog> listBlogsByQuery(String query,Pageable pageable);

    List<Blog> listRecommendBlogTop(Integer size);

    Blog saveBlog(Blog blog);

    Blog updateBlog(Long id,Blog blog);

    Blog updateBlog(Blog blog);

    List<Blog> getBlogsByYear(String year);
    //获取年份分组的字符串集
    List<String> getgrouByYear();

    Long count();

    Long getNextId();

    boolean deleteById(Long id);
}
