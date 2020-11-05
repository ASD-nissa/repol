package com.example.web;

import com.example.EntityClass.Blog;
import com.example.service.BlogService;
import com.example.utils.CreateNewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/archives")
public class ArchivesController {

    @Autowired
    BlogService blogService;

    @ResponseBody
    @GetMapping("/json")
    public Object archivesJson(){
        Map retMap = new HashMap();
        List<String> years =  blogService.getgrouByYear();
        HashMap<String,List<Blog>> mapBLog = new HashMap<>();
        for(String year : years){
            List<Blog> blogs = blogService.getBlogsByYear(year);
            mapBLog.put(year, CreateNewObject.CreateNewBlogLit(blogs));
        }
        retMap.put("mapBlog",mapBLog);
        retMap.put("size",blogService.count());
        return retMap;
    }

    @GetMapping
    public String archives(Model model){
        List<String> years =  blogService.getgrouByYear();
        HashMap<String,List<Blog>> mapBLog = new HashMap<>();
        for(String year : years){
            List<Blog> blogs = blogService.getBlogsByYear(year);
            mapBLog.put(year,blogs);
        }
        model.addAttribute("mapBLog",mapBLog);
        model.addAttribute("count",blogService.count());
        return "archives";
    }
}
