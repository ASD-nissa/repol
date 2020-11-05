package com.example.service.imp;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Tag;
import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import com.example.Exception.NoFonudException;
import com.example.dao.BlogRepository;
import com.example.service.BlogService;
import com.example.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    BlogRepository blogRepository;


    @Override
    public Blog getBolgById(Long id) {
        try{
            return blogRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e){
            return null;
        }
    }

    @Override
    public Blog getBolgByTitle(String title) {
        return blogRepository.findByTitle(title);
    }

    @Override
    public Page<Blog> listBlogs(Pageable pageable, BlogQuery blog, User user) {

        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(!"".equals(blog.getTitle()) && blog.getTitle()!=null){
                    predicates.add(cb.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }

                if(blog.getTypeId() != null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }

                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("recommend"),true));
                }

                //不是管理员时
                if(user.getType() != 1)
                    predicates.add(cb.equal(root.<User>get("user").<Long>get("id"),user.getId()));

                Predicate[] predicateArray = new Predicate[predicates.size()];
                for(int i=0;i<predicates.size();i++)
                    predicateArray[i] = predicates.get(i);

                cq.where(predicateArray);
                return null;
            }
        },pageable);
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        Blog b = getBolgById(blog.getId());
        if(b == null)
            throw new NoFonudException("不存在blog数据");

        blog.setCreateTime(b.getCreateTime());
        BeanUtils.copyProperties(blog,b);
        return saveBlog(b);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = getBolgById(id);
        if(b == null)
            throw new NoFonudException("不存在blog数据");

        Blog blogModel = new Blog();

        if(blog.getImgName() != blogModel.getImgName())
            b.setImgName(blog.getImgName());
        if(blog.getTitle() != blogModel.getTitle())
            b.setTitle(blog.getTitle());
        if(blog.getFristPicture() != blogModel.getFristPicture())
            b.setFristPicture(blog.getFristPicture());
        if(blog.getFlag() != blogModel.getFlag())
            b.setFlag(blog.getFlag());
        if(blog.getContent() != blogModel.getContent())
            b.setContent(blog.getContent());
        if(blog.getViews() != blogModel.getViews())
            b.setViews(blog.getViews());
        if(blog.getUser() != blogModel.getUser())
            b.setUser(blog.getUser());
        if(blog.getTags() != blogModel.getTags())
            b.setTags(blog.getTags());
        if(blog.getComments() != blogModel.getComments())
            b.setComments(blog.getComments());
        if(blog.getType() != blogModel.getType())
            b.setType(blog.getType());
        if(blog.getAuthorDetails() != blogModel.getAuthorDetails())
            b.setAuthorDetails(blog.getAuthorDetails());
        if(blog.getAppreciate() != blogModel.getAppreciate())
            b.setAppreciate(blog.getAppreciate());
        if(blog.getStatement() != blogModel.getStatement())
            b.setStatement(blog.getStatement());
        if(blog.getCommentabled() != blogModel.getCommentabled())
            b.setCommentabled(blog.getCommentabled());
        if(blog.getState() != blogModel.getState())
            b.setState(blog.getState());
        if(blog.getRecommend() != blogModel.getRecommend())
            b.setRecommend(blog.getRecommend());
        if(blog.getUpdateTime() != blogModel.getUpdateTime()){
            b.setUpdateTime(blog.getUpdateTime());
        }
        if(blog.getCreateTime() != blogModel.getCreateTime()){
            b.setCreateTime(blog.getCreateTime());
        }

        return saveBlog(b);
    }

    @Override
    public Page<Blog> listBlogs(Pageable pageable) {
        return blogRepository.findAllByState(pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable = PageRequest.of(0,size,sort);

        return blogRepository.findTop(pageable);
    }

    @Override
    public Page<Blog> listBlogsByQuery(String query, Pageable pageable) {
        return blogRepository.findByLikeQuery("%"+query+"%",pageable);
    }

    @Override
    public Page<Blog> listBlogsByType(Pageable pageable,Type type) {
        return blogRepository.findBlogsByTypeAndState(pageable,type,true);
    }

    @Override
    public Page<Blog> listBlogsByTagId(Long id,Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Join join = root.join("tags");
                predicates.add(cb.equal(join.get("id"),id));
                predicates.add(cb.equal(root.<Boolean>get("state"),true));

                Predicate[] predicateArray = new Predicate[predicates.size()];
                for(int i=0;i<predicates.size();i++)
                    predicateArray[i] = predicates.get(i);
                cq.where(predicateArray);
                return null;
            }
        },pageable);
    }

    @Override
    public List<Blog> getBlogsByYear(String year) {
        return blogRepository.findBlogsByYear(year);
    }

    @Override
    public List<String> getgrouByYear() {
        return blogRepository.grouByYear();
    }

    @Override
    public Long count() {
        return blogRepository.count();
    }

    @Override
    public Long getNextId() {
        return blogRepository.findNextId();
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        try{
            blogRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
