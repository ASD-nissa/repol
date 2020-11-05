package com.example.dao;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Tag;
import com.example.EntityClass.Type;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long>, JpaSpecificationExecutor<Blog> {

    Blog findByTitle(String title);

    Page<Blog> findBlogsByTypeAndState(Pageable pageable, Type type,boolean state);

    @Query("select b from Blog b where b.recommend = true")
    List<Blog> findTop(Pageable pageable);

    @Query("select b from Blog b where b.state = true")
    Page<Blog> findAllByState(Pageable pageable);

    @Query("select b from Blog b where (b.title like ?1 or b.content like ?1) and b.state = true")
    Page<Blog> findByLikeQuery(String query,Pageable pageable);

    @Query("select  function('date_format',b.updateTime,'%Y') as year from Blog b group by year order by year desc")
    List<String> grouByYear();

    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y') = ?1 ")
    List<Blog> findBlogsByYear(String year);

    @Query(nativeQuery=true,value="select  * from hibernate_sequence limit 1 ")
    Long findNextId();
}
