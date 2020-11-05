package com.example.web;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Type;
import com.example.service.BlogService;
import com.example.service.TypeService;
import com.example.utils.CreateNewObject;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSException;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/types")
public class TypesController {

    @Autowired
    private TypeService typeService;
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String Types(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        HttpSession session, Model model){
        List<Type> types = typeService.listTop(1);
        if(types == null || types.size() == 0)
            return "redirect:/";
        Long id = types.get(0).getId();
        return TypesById(id,pageable,model);
    }



    @GetMapping("/{id}")
    public String TypesById(@PathVariable Long id,
                            @PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            Model model){
        model.addAttribute("types",typeService.getAll());
        model.addAttribute("page",blogService.listBlogsByType(pageable,typeService.getTypeById(id)));
        model.addAttribute("id",id);

        return "types";
    }

    @GetMapping("/json")
    @ResponseBody
    public Object TypesJson(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam("id") Long id){
        Map retMap = new HashMap();
        if(id < 0){
            List<Type> types = typeService.listTop(1);
            if(types == null || types.size() == 0)
                return retMap;
            id = types.get(0).getId();
        }

        //获取All Type
        List<Type> types = typeService.getAll();
        retMap.put("types",CreateNewObject.CreateNewTypeList(types));

        //获取blog展示列表
        Page<Blog> blogs = blogService.listBlogsByType(pageable,typeService.getTypeById(id));
        retMap.put("blogs",CreateNewObject.CreateNewBlogLit(blogs.getContent()));

        //取出当前页数
        int pageNumber = blogs.getNumber();
        //是否为首页
        boolean isFirst = blogs.isFirst();
        //是否为最后一页
        boolean isLast = blogs.isLast();
        //篇幅是否大于一页
        boolean isPages = blogs.getTotalPages()>1;

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);
        retMap.put("isPages",isPages);
        retMap.put("id",id);
        return retMap;
    }

}
