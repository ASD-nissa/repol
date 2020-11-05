package com.example.service.imp;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import com.example.Exception.NoFonudException;
import com.example.dao.TypeRepository;
import com.example.service.TypeService;
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
import java.util.List;
import java.util.Optional;

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Transactional
    @Override
    public Type saveType(Type type) {

        return typeRepository.save(type);
    }

    @Override
    public Type getTypeById(Long id) {
        try{
           return typeRepository.findById(id).get();
        }catch (java.util.NoSuchElementException e){
            return null;
        }
    }

    @Override
    public Type getTypeByname(String name) {
        return typeRepository.findByName(name);
    }

    @Override
    public Page<Type> listType(Pageable pageable, User user) {

        return typeRepository.findAll(new Specification<Type>() {
            @Override
            public Predicate toPredicate(Root<Type> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate predicate = null;
                //不是管理员时
                if(user.getType() != 1)
                    predicate = cb.equal(root.<User>get("user").<Long>get("id"),user.getId());
                return predicate;
            }
        },pageable);
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        Type t = getTypeById(id);
        if(t == null) {
            throw new NoFonudException("不存在该type数据");
        }

        BeanUtils.copyProperties(type,t);
        return saveType(t);
    }

    @Override
    public boolean deleteType(Long id) {
        try{
            typeRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public List<Type> listTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);
        return typeRepository.findTop(pageable);
    }

    @Override
    public List<Type> getAll(User user) {
        return typeRepository.findAll(new Specification<Type>() {
            @Override
            public Predicate toPredicate(Root<Type> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<User>get("user").<Long>get("id"),user.getId());
            }
        });
    }

    public List<Type> getAll() {
        return typeRepository.findAll();
    }
}
