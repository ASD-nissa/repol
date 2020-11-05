package com.example.dao;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Comment;
import com.example.EntityClass.Type;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long>, JpaSpecificationExecutor<Comment> {

    @Query("select c from Comment c where c.parentComment is null and blog_id=?1")
    List<Comment> findAllByIdAndNoSon(Long id);
}
