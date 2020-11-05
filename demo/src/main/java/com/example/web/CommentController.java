package com.example.web;

import com.example.EntityClass.Comment;
import com.example.EntityClass.User;
import com.example.service.BlogService;
import com.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;
    @Autowired
    BlogService blogService;
    
    @PostMapping
    public String commentPost(Comment comment, @RequestParam("blogId") Long blogId,
                              @RequestParam("parentCommentId") Long parentCommentId,
                              Model model,HttpSession session){
        if(parentCommentId != -1) {
            comment.setParentComment(commentService.getCommentById(parentCommentId));
        }
        comment.setBlog(blogService.getBolgById(blogId));
        User user = (User)session.getAttribute("user");
        if(user == null)
            comment.setImg("/images/AKL.jpg");
        else
            comment.setImg(user.getImg());
        commentService.save(comment);
        model.addAttribute("comments",commentService.getAllByblogIdAndNoSon(blogId));
        return "blog :: commentContainer";
    }

    @ResponseBody
    @PostMapping("/json")
    public Object commentPostJson(Comment comment, @RequestParam("blogId") Long blogId,
                              @RequestParam("parentCommentId") Long parentCommentId,
                             HttpSession session){
        Map retMap = new HashMap();

        if(parentCommentId != -1) {
            comment.setParentComment(commentService.getCommentById(parentCommentId));
        }
        comment.setBlog(blogService.getBolgById(blogId));
        User user = (User)session.getAttribute("user");
        if(user == null)
            comment.setImg("/images/AKL.jpg");
        else
            comment.setImg(user.getImg());
        commentService.save(comment);
        List<Comment> comments= commentService.getAllByblogIdAndNoSon(blogId);
        List<Comment> commentList = getNewCommentList(comments);
        retMap.put("comments",commentList);
        return retMap;
    }

    public List<Comment> getNewCommentList(List<Comment> comments){
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

    @GetMapping
    public String commentGet(Comment comment, @RequestParam("blogId") Long blogId,
                             @RequestParam("parentCommentId") Long parentCommentId,
                             Model model, HttpSession session){
        return commentPost(comment,blogId,parentCommentId,model,session);
    }

    @GetMapping("/delete/json")
    @ResponseBody
    public Object deleteGetJson(@RequestParam("id") Long id){
        return deletePostJson(id);
    }

    @PostMapping("/delete/json")
    @ResponseBody
    public Object deletePostJson(@RequestParam("id") Long id){
        Map retMap = new HashMap();
        Comment comment = commentService.getCommentById(id);
        if(comment.getSonComments().size() > 0){
            for(Comment sonComment : comment.getSonComments()){
                commentService.deleteById(sonComment.getId());
            }
        }
        commentService.deleteById(id);
        List<Comment> commentList = getNewCommentList(commentService.getAllByblogIdAndNoSon(comment.getBlog().getId()));
        retMap.put("comments",commentList);
        return retMap;
    }

    @GetMapping("/delete/")
    public String deleteGet(@RequestParam("id") Long id,Model model){
        return deletePost(id,model);
    }

    @PostMapping("/delete/")
    public String deletePost(@RequestParam("id") Long id,Model model) {
        Comment comment = commentService.getCommentById(id);
        if(comment.getSonComments().size() > 0){
            for(Comment sonComment : comment.getSonComments()){
                commentService.deleteById(sonComment.getId());
            }
        }
        commentService.deleteById(id);
        model.addAttribute("comments",commentService.getAllByblogIdAndNoSon(comment.getBlog().getId()));
        return "blog :: commentContainer";
    }

}
