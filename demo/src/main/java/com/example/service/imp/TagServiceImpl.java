package com.example.service.imp;

import com.example.EntityClass.Tag;
import com.example.EntityClass.User;
import com.example.Exception.NoFonudException;
import com.example.dao.TagRepository;
import com.example.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag getTagById(Long id) {
        try{
            return tagRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e){
            return null;
        }
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public List<Tag> getAll(User user) {
        return tagRepository.findAll(new Specification<Tag>() {
            @Override
            public Predicate toPredicate(Root<Tag> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<User>get("user").<Long>get("id"),user.getId());
            }
        });
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> ListByIds(String ids) {
        if(ids == null || "".equals(ids))
            return null;
        List<Long> listId = new ArrayList<>();
        String[] idArray = ids.split(",");
        for(int i=0;i<idArray.length;i++)
            listId.add(new Long(idArray[i]));

        List<Tag> tags = new ArrayList<>();
        for(Long id : listId)
            tags.add(getTagById(id));

        return tags;
    }

    @Override
    public Page<Tag> listTags(Pageable pageable,User user) {
        return tagRepository.findAll(new Specification<Tag>() {
            @Override
            public Predicate toPredicate(Root<Tag> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = null;
                //不是管理员时
                if(user.getType() != 1)
                    predicate = criteriaBuilder.equal(root.<User>get("user").<Long>get("id"),user.getId());
                return predicate;
            }
        }, pageable);
    }

    @Override
    public List<Tag> listTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);

        return tagRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Tag updateTag(Tag tag) {
        Tag t = getTagById(tag.getId());
        if(t == null){
            throw new NoFonudException("不存在改tag数据");
        }

        BeanUtils.copyProperties(tag,t);
        return saveTag(t);
    }

    @Override
    public boolean delete(Long id) {
        try{
            tagRepository.deleteById(id);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

}
