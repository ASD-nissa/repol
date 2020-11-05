package com.example.service;

import com.example.EntityClass.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User checkUser(String username, String password);

    int chechUserNoNicknameOrUsername(String nickname,String username);

    User getUserById(Long id);

    User save(User user);

    User update(User user);

    Page<User> getAll(Pageable pageable);

    List<User> getAllbyAdmin();

    boolean deleteById(Long id);
}
