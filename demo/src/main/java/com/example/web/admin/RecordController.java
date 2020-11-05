package com.example.web.admin;

import com.example.EntityClass.Record;
import com.example.service.RecordService;
import com.example.utils.CreateNewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/records")
public class RecordController {

    @Autowired
    RecordService recordService;

    @ResponseBody
    @GetMapping("/json")
    public Object usersJson(@PageableDefault(size = 8,sort = {"id"},direction = Sort.Direction.ASC) Pageable pageable){
        Map retMap = new HashMap();
        Page<Record> Records = recordService.getAll(pageable);
        retMap.put("records", Records.getContent());

        //取出当前页数
        int pageNumber = Records.getNumber();
        //是否为首页
        boolean isFirst = Records.isFirst();
        //是否为最后一页
        boolean isLast = Records.isLast();

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        return retMap;
    }

    @ResponseBody
    @GetMapping("/json/delete")
    public Object deleteJson(@RequestParam("id") Long id, RedirectAttributes attributes){
        delete(id,attributes);
        return  attributes.getFlashAttributes();
    }

    @GetMapping
    public String records(@PageableDefault(size = 20,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable, Model model){

        model.addAttribute("page",recordService.getAll(pageable));
        return "admin/records";
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes attributes){
        boolean t = recordService.deleteById(id);
        if(t){
            attributes.addFlashAttribute("message","删除成功");
        }else{
            attributes.addFlashAttribute("message","删除失败");
        }
        return "redirect:/admin/records";
    }
}
