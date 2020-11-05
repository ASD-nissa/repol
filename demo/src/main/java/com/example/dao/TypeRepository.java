package com.example.dao;


import com.example.EntityClass.Blog;
import com.example.EntityClass.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type,Long>, JpaSpecificationExecutor<Type> {
    Type findByName(String name);


    List<Type> findAll();

    @Query("select t from Type t")
    List<Type> findTop(Pageable pageable);
}
