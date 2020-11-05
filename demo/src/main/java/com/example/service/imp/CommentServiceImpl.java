package com.example.service.imp;

import com.example.EntityClass.Comment;
import com.example.dao.CommentRepository;
import com.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment getCommentById(long id) {
        try {
            return commentRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public List<Comment> getAllByblogId(Long id) {
        return commentRepository.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<Comment>get("blog").<Long>get("id"),id);
            }
        });
    }

    @Override
    public List<Comment> getAllByblogIdAndNoSon(Long id) {
        return commentRepository.findAllByIdAndNoSon(id);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            commentRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
