package com.example.web;

import com.example.EntityClass.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.List;

@Controller
public class registerController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user",new User());
        return "register";
    }

    @ResponseBody
    @PostMapping("/json/saveRegister")
    public Object saveRegisterJson(@Valid User user, BindingResult result, @RequestParam("file") MultipartFile file){
        saveRegister(user,result,file);
        return result.getAllErrors();
    }

    @PostMapping("/saveRegister")
    public String saveRegister(@Valid User user, BindingResult result, @RequestParam("file") MultipartFile file){
        if(result.hasErrors()){
            return "register";
        }

        int c = userService.chechUserNoNicknameOrUsername(user.getNickname(),user.getUsername());
        if(c != 0){
            if(c == 1){
                result.rejectValue("username","usernameError","用户名重复");
            }else{
                result.rejectValue("nickname","nicknameError","昵称重复");
            }

            return "register";
        }

        String imgName = file.getOriginalFilename();
        String[] imgNames = imgName.split("\\\\");
        imgName = imgNames[imgNames.length-1];
        File imgFile = null;
        try{
            String path = ResourceUtils.getURL("classpath:").getPath();
            imgFile = new File(path+"static/images/"+imgName);
            while(imgFile.exists()){
                imgName = Math.abs(Math.random())*100+imgName;
                imgFile = new File(path+"static/images/"+imgName);
            }
            file.transferTo(imgFile);
        } catch (Exception e) {
            result.rejectValue("nickname","","图片保存失败");
            return "register";
        }

        user.setImg("/images/"+imgName);
        if(userService.getAllbyAdmin().size() == 0)
            user.setType(1);
        userService.save(user);

        return "admin/login";
    }
}
