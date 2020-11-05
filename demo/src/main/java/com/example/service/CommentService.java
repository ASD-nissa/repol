package com.example.service;

import com.example.EntityClass.Comment;
import org.hibernate.IdentifierLoadAccess;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CommentService {

    Comment getCommentById(long id);

    Comment save(Comment comment);

    List<Comment> getAllByblogId(Long id);

    List<Comment> getAllByblogIdAndNoSon(Long id);

    boolean deleteById(Long id);

}
