package com.example.web.admin;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Tag;
import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import com.example.service.BlogService;
import com.example.service.TagService;
import com.example.service.TypeService;
import com.example.service.UserService;
import com.example.utils.CreateNewObject;
import com.example.utils.FIleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;


    @ResponseBody
    @GetMapping("/json")
    public Object usersJson(@PageableDefault(size = 8,sort = {"id"},direction = Sort.Direction.ASC) Pageable pageable){
        Map retMap = new HashMap();
        Page<User> users = userService.getAll(pageable);
        retMap.put("users", CreateNewObject.CreateNewUserList(users.getContent()));

        //取出当前页数
        int pageNumber = users.getNumber();
        //是否为首页
        boolean isFirst = users.isFirst();
        //是否为最后一页
        boolean isLast = users.isLast();

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        return retMap;
    }

    @ResponseBody
    @GetMapping("/json/delete")
    public Object deleteJson(@RequestParam("id") Long id,RedirectAttributes attributes) throws FileNotFoundException{
        delete(id,attributes);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @PostMapping("/json/saveEditorUserPassword")
    public Object saveEditorUserPassword(@RequestParam("id") Long id, @RequestParam("oldPassword") String oldPassword,@RequestParam("password") String password, HttpSession session){
        Map<String,Object> retMap = new HashMap<>();
        User sessionUser = (User)session.getAttribute("user");
        if(sessionUser.getId() != id && sessionUser.getType() != 1){
            retMap.put("code",0);
            retMap.put("message","权限不足");
            return retMap;
        }

        if(userService.checkUser(sessionUser.getUsername(),oldPassword) == null){
            retMap.put("code",0);
            retMap.put("message","原密码错误");
            return  retMap;
        }

        boolean isMyAccount = sessionUser.getId() == id;
        User changeUser = userService.getUserById(id);
        changeUser.setPassword(password);
        if(userService.update(changeUser) == null){
            retMap.put("code",0);
            retMap.put("message","更新失败");
        }else{
            if(isMyAccount) {
                retMap.put("code",1);
            }else{
                retMap.put("code",2);
                retMap.put("MyAccount",sessionUser.getUsername());
                retMap.put("targetAccount",changeUser.getUsername());
            }
            retMap.put("message","更新成功");
        }
        return retMap;
    }

    @ResponseBody
    @PostMapping("/json/saveEditorUserImg")
    public Object saveEditorUserImg(@RequestParam("id") Long id,
                                    @RequestParam("imgFile") MultipartFile imgFile,
                                    HttpSession session) throws FileNotFoundException {
        Map<String,Object> retMap = new HashMap<>();
        User sessionUser = (User)session.getAttribute("user");

        if(id != sessionUser.getId()){
            retMap.put("code",0);
            retMap.put("message","权限不足");
            retMap.put("target",sessionUser.getUsername());
            return retMap;
        }

        String fileName = imgFile.getOriginalFilename();
        String[] fileNames = fileName.split("\\\\");
        fileName = fileNames[fileNames.length-1];

        //删除原文件
        String classPath = ResourceUtils.getURL("classpath:").getPath();
        String staticPath = classPath+"static/images/";
        String oldImgPath = classPath + "static/" + sessionUser.getImg();
        File oldImgFile = new File(oldImgPath);
        while(oldImgFile.exists()){
            oldImgFile.delete();
        }

        //保存新文件
        String newImgPath = staticPath + fileName;
        File newImgFile = new File(newImgPath);
        try {
            imgFile.transferTo(newImgFile);
        }catch (IOException e){
            retMap.put("code",0);
            retMap.put("message","保存出错");
            retMap.put("error",e.getMessage());
            return retMap;
        }

        //更新数据库
        User changeUser = userService.getUserById(id);
        changeUser.setImg("/images/"+fileName);
        if(userService.update(changeUser)==null){
            retMap.put("code",0);
            retMap.put("message","数据库更新失败");
            return retMap;
        }

        sessionUser.setImg(changeUser.getImg());
        retMap.put("code",1);
        retMap.put("message","更新成功");
        retMap.put("imgName",changeUser.getImg());
        return retMap;
    }

    @GetMapping
    public String users(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.ASC) Pageable pageable, Model model){
        model.addAttribute("page",userService.getAll(pageable));

        return "admin/users";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) throws FileNotFoundException {
        User user = userService.getUserById(id);
        if(user.getType() == 1) {
            attributes.addFlashAttribute("message","不能删除管理员用户");
            return "redirect:/admin/users";
        }
        List<Blog> blogs = user.getBlogs();
        List<Type> types = user.getTypes();
        List<Tag> tags = user.getTags();

        String path = ResourceUtils.getURL("classpath:").getPath();
        for(Blog blog: blogs){
            String FirstPicture = blog.getFristPicture();
            File Imgfile = new File(path + "static/" + FirstPicture);
            if (Imgfile.exists())
                Imgfile.delete();
            FIleUtil.delete(path+"static/images/blogImg/"+blog.getImgName());
            blogService.deleteById(blog.getId());
        }
        for(Type type : types){
            typeService.deleteType(type.getId());
        }
        for(Tag tag : tags){
            tagService.delete(tag.getId());
        }

        String userImgName = user.getImg();
        File Imgfile = new File(path + "static/" + userImgName);
        if(Imgfile.exists())
            Imgfile.delete();
        if(userService.deleteById(user.getId()))
            attributes.addFlashAttribute("message","删除成功");
        else
            attributes.addFlashAttribute("message","删除失败");
        return "redirect:/admin/users";
    }


}
