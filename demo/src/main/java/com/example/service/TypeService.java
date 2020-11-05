package com.example.service;

import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    Type saveType(Type type);

    Type getTypeById(Long id);

    Type getTypeByname(String name);

    List<Type> getAll(User user);

    List<Type> getAll();

    List<Type> listTop(Integer size);

    Page<Type> listType(Pageable pageable, User user);

    Type updateType(Long id,Type type);

    boolean deleteType(Long id);
}
