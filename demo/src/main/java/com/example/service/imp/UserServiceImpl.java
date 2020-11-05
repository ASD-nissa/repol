package com.example.service.imp;

import com.example.EntityClass.User;
import com.example.Exception.NoFonudException;
import com.example.dao.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username,password);
        return user;
    }

    @Override
    public int chechUserNoNicknameOrUsername(String nickname, String username) {
        if(userRepository.findByUsername(username) != null)
            return 1;
        if(userRepository.findByNickname(nickname) != null)
            return -1;
        return 0;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            userRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    @Override
    public List<User> getAllbyAdmin() {
        return userRepository.findByType(1);
    }

    @Override
    public User update(User user) {
        User u = userRepository.getOne(user.getId());
        if(u == null){
            throw new NoFonudException("不存在该账户");
        }

        BeanUtils.copyProperties(user,u);
        return save(u);
    }
}
