package com.example.web;

import com.example.EntityClass.Blog;
import com.example.EntityClass.Tag;
import com.example.EntityClass.User;
import com.example.service.BlogService;
import com.example.service.TagService;
import com.example.service.TypeService;
import com.example.utils.CreateNewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String Tags(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        List<Tag> tags = tagService.listTop(1);
        if(tags == null || tags.size() == 0)
            return "redirect:/";
        Long id = tags.get(0).getId();
        return TagsById(id,pageable,model);
    }
    @GetMapping("/{id}")
    public String TagsById(@PathVariable Long id,
                           @PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            Model model){
        model.addAttribute("tags",tagService.getAll());
        Page<Blog> blogs = blogService.listBlogsByTagId(tagService.getTagById(id).getId(),pageable);
        model.addAttribute("page",blogs);
        model.addAttribute("id",id);

        return "tags";
    }

    @ResponseBody
    @GetMapping("/json")
    public Object TypesJson(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam("id") Long id){
        Map retMap = new HashMap();
        if(id < 0){
            List<Tag> tags = tagService.listTop(1);
            if(tags == null || tags.size() == 0)
                return retMap;
            id = tags.get(0).getId();
        }

        //获取All Tag
        List<Tag> tags = tagService.getAll();
        retMap.put("tags", CreateNewObject.CreateNewTagList(tags));

        //获取blog展示列表
        Page<Blog> blogs = blogService.listBlogsByTagId(id,pageable);
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
