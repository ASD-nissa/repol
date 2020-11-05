package com.example.dao;

import com.example.EntityClass.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsernameAndPassword(String username,String password);

    User findByUsername(String username);

    User findByNickname(String nickname);

    List<User> findByType(Integer type);

}
