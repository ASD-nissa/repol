package com.example.web.admin;

import com.example.EntityClass.User;
import com.example.service.UserService;
import com.example.service.imp.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class loginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String loginPage(HttpSession session){
        if(session.getAttribute("user") != null)
            return "admin/index";
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes){
        System.out.println("login");
        User user = userService.checkUser(username,password);
        if(user != null) {
            user.setPassword(null);
            session.setAttribute("user", user);
            return "admin/index";
        }

        attributes.addFlashAttribute("message","用户名或密码错误");
        return "redirect:/admin";
    }

    @ResponseBody
    @PostMapping("/login/json")
    public Object loginJson(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            HttpSession session){
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials","true");
        Map retMap = new HashMap<>();

        System.out.println("loginJson");
        User user = userService.checkUser(username,password);
        if(user != null) {
            user.setPassword(null);
            session.setAttribute("user", user);
            User nUser = new User();
            nUser.setNickname(user.getNickname());
            nUser.setImg(user.getImg());
            nUser.setType(user.getType());
            nUser.setEmail(user.getEmail());
            nUser.setId(user.getId());
            retMap.put("user",nUser);
            retMap.put("sessionId",session.getId());
            retMap.put("msg",true);
        }else{
            retMap.put("msg",false);
        }

        return retMap;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response,HttpSession session){
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials","true");
        System.out.println(session.getAttribute("user"));
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
