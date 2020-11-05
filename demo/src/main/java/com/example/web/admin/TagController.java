package com.example.web.admin;

import com.example.EntityClass.Tag;
import com.example.EntityClass.User;
import com.example.service.TagService;
import com.example.utils.CreateNewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @ResponseBody
    @GetMapping("/json")
    public Object tagJson(@PageableDefault(size = 8,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                           HttpSession session){
        Map retMap = new HashMap();
        User user = (User)session.getAttribute("user");
        Page<Tag> tags = tagService.listTags(pageable,user);
        retMap.put("tags", CreateNewObject.CreateNewTagList(tags.getContent()));

        //取出当前页数
        int pageNumber = tags.getNumber();
        //是否为首页
        boolean isFirst = tags.isFirst();
        //是否为最后一页
        boolean isLast = tags.isLast();

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        return retMap;
    }

    @ResponseBody
    @PostMapping("/json/save")
    public Object saveTagJson(@Valid Tag tag, BindingResult result, HttpSession session, RedirectAttributes attributes){
        saveTag(tag,result,session,attributes);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @PostMapping("/json/saveEditor")
    public Object saveEditorJson(@Valid Tag tag,
                                 BindingResult result,
                                 @RequestParam("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes attributes, Model model){
        saveEditor(id,tag,result,session,attributes);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @GetMapping("/json/delete")
    public Object deleteJson(@RequestParam("id") Long id, HttpSession session,RedirectAttributes attributes){
        delete(id,session,attributes);
        return attributes.getFlashAttributes();
    }

    @GetMapping
    public String tags(@PageableDefault(size = 3,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                       HttpSession session,Model model){
        User user = (User)session.getAttribute("user");
        model.addAttribute("page",tagService.listTags(pageable,user));
        return "admin/tags";
    }

    @GetMapping("input")
    public String input(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,RedirectAttributes attributes){
        User user = (User) session.getAttribute("user");
        boolean t = false;
        if(user.getType() == 1 || tagService.getTagById(id).getUser().getId() == user.getId())
            t = tagService.delete(id);

        if(t){
            attributes.addFlashAttribute("message","删除成功");
        }else{
            attributes.addFlashAttribute("message","删除失败");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/{id}/input")
    public String editorTag(@PathVariable Long id,Model model){
        Tag t = tagService.getTagById(id);
        model.addAttribute("tag",t);
        return "admin/tags-input";
    }

    @PostMapping
    public String saveTag(@Valid Tag tag, BindingResult result, HttpSession session,RedirectAttributes attributes){
        if(result.hasErrors()){
            return "admin/tags";
        }


        Tag t = null;
        //验证重复标题用户是否相同 不相同时通过
        User user = (User) session.getAttribute("user");
        tag.setUser(user);
        t = tagService.getTagByName(tag.getName());
        if(t == null /*|| t.getUser().getId() != user.getId()*/) {
            t = null;
            t = tagService.saveTag(tag);
        }else{
            result.rejectValue("name","nameError","命名重复");
            attributes.addFlashAttribute("message","命名重复");
            return "admin/tags-input";
        }

        if(t == null){
            attributes.addFlashAttribute("message","新增失败");
        }else{
            attributes.addFlashAttribute("message","新增成功");
        }

        return "redirect:/admin/tags";
    }

    @PostMapping("/{id}")
    public String saveEditor(@PathVariable Long id,@Valid Tag tag, BindingResult result, HttpSession session, RedirectAttributes attributes){
        if(result.hasErrors()){
            return "admin/tags-input";
        }
        //验证重复标题用户是否相同 不相同时通过
        User user = (User) session.getAttribute("user");
        tag.setUser(user);
        Tag t =null;
        t = tagService.getTagByName(tag.getName());
        if(t!= null /*&& t.getUser().getId()==user.getId()*/){
            result.rejectValue("name","nameError","命名重复");
            attributes.addFlashAttribute("message","命名重复");
            return "admin/tags-input";
        }

        t = null;
        if(tagService.getTagById(id).getUser().getId() == user.getId()){
            tag.setUser((User)session.getAttribute("user"));
            t = tagService.updateTag(tag);
        }

        if(t == null){
            attributes.addFlashAttribute("message","修改失败");
        }else{
            attributes.addFlashAttribute("message","修改成功");
        }

        return "redirect:/admin/tags";
    }

}
