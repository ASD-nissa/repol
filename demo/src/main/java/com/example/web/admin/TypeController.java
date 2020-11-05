package com.example.web.admin;

import com.example.EntityClass.Type;
import com.example.EntityClass.User;
import com.example.service.TypeService;
import com.example.utils.CreateNewObject;
import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.lang.model.element.TypeElement;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/types")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @ResponseBody
    @GetMapping("/json")
    public Object typeJson(@PageableDefault(size = 8,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                           HttpSession session){
        Map retMap = new HashMap();
        User user = (User)session.getAttribute("user");
        Page<Type> types = typeService.listType(pageable,user);
        retMap.put("types", CreateNewObject.CreateNewTypeList(types.getContent()));

        //取出当前页数
        int pageNumber = types.getNumber();
        //是否为首页
        boolean isFirst = types.isFirst();
        //是否为最后一页
        boolean isLast = types.isLast();

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        return retMap;
    }

    @ResponseBody
    @PostMapping("/json/save")
    public Object saveTypeJson(@Valid Type type, BindingResult result, HttpSession session, RedirectAttributes attributes, Model model){
        saveType(type,result,session,attributes,model);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @PostMapping("/json/saveEditor")
    public Object saveEditorJson(@Valid Type type,
                                 BindingResult result,
                                 @RequestParam("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes attributes, Model model){
        saveEditor(type,result,id,session,attributes,model);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @GetMapping("/json/delete")
    public Object deleteJson(@RequestParam("id") Long id, HttpSession session,RedirectAttributes attributes){
        delete(id,session,attributes);
        return attributes.getFlashAttributes();
    }

    @GetMapping()
    public String types(@PageableDefault(size = 3,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                       HttpSession session,Model model){
        User user = (User)session.getAttribute("user");
        model.addAttribute("page",typeService.listType(pageable,user));
        return "admin/types";
    }

    @GetMapping("/input")
    public String input(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    @PostMapping
    public String saveType(@Valid Type type, BindingResult result, HttpSession session, RedirectAttributes attributes, Model model){
        Type t=null;
        User user = (User)session.getAttribute("user");
        type.setUser(user);
        t = typeService.getTypeByname(type.getName());
        if(t == null /*|| t.getUser().getId() != user.getId()*/) {
            t = typeService.saveType(type);
        } else{
            result.rejectValue("name","nameError","该名称已存在");
            attributes.addFlashAttribute("message","该名称已存在");
            return "/admin/types-input";
        }

        if(result.hasErrors()){
            return "/admin/types-input";
        }

        if(t == null){
            attributes.addFlashAttribute("message","操作失败");
        }else{
            attributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/types";
    }

    @PostMapping("/{id}")
    public String saveEditor(@Valid Type type, BindingResult result, @PathVariable Long id, HttpSession session, RedirectAttributes attributes,Model model){
        if(result.hasErrors()){
            return "/admin/types-input";
        }

        Type t=null;
        User user = (User)session.getAttribute("user");
        type.setUser(user);
        t = typeService.getTypeByname(type.getName());

        if(t != null /*&& t.getUser().getId() == user.getId()*/) {
           result.rejectValue("name","nameError","该名称已存在");
           attributes.addFlashAttribute("message","该名称已存在");
           return "admin/types-input";
       }

        t = null;

        if(typeService.getTypeById(id).getUser().getId() == user.getId()){
            type.setUser((User)session.getAttribute("user"));
            t =  typeService.updateType(id,type);
        }
        if(t == null){
            attributes.addFlashAttribute("message","修改失败");
        }else{
            attributes.addFlashAttribute("message","修改成功");
        }

        return "redirect:/admin/types";
    }

    @GetMapping("/{id}/input")
    public String editorType(@PathVariable Long id,Model model){
        model.addAttribute("type",typeService.getTypeById(id));
        return "admin/types-input";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,RedirectAttributes attributes){
        User user = (User) session.getAttribute("user");
        boolean t = false;
        if(user.getType() == 1 || typeService.getTypeById(id).getUser().getId() == user.getId())
            t = typeService.deleteType(id);

        if(t){
            attributes.addFlashAttribute("message","删除成功");
        }else{
            attributes.addFlashAttribute("message","删除失败");
        }

        return "redirect:/admin/types";
    }
}
