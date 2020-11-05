package com.example.web;

import com.example.EntityClass.superstar.Question;
import com.example.annotation.NoAspect;
import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/update")
public class PublicUpdateController {
    @Autowired
    QuestionService questionService;

    @NoAspect
    @PostMapping("/superstar")
    @ResponseBody
    public Map<String,Object> updateQuestion(@RequestParam("question") String question,
                                             @RequestParam("da") String da,
                                             @RequestParam("update") Boolean update,
                                             HttpServletResponse response,
                                             HttpSession session){
        response.setHeader("Access-Control-Allow-Origin","*");
        Map<String,Object> map = new HashMap<>();
        boolean flag;//数据库中有记录为真
        try {
             flag= questionService.getOne(question, da) != null;
        }catch (IncorrectResultSizeDataAccessException e){
            List<Question> questions = questionService.getList(question,da);
            for (int i = 1; i < question.length(); i++) {
                questionService.delete(questions.get(i));
            }
             flag = true;
        }
        if(da.indexOf("目前没思路")!=-1||
                "".equals(da)||
                "".equals(question)||
                question==null||da==null|| flag) {
            map.put("status", "400");
            if(!flag)
                map.put("msg","上传内容为空");
            else if(!update)
                map.put("msg","上传内容重复");
            else{
                Question buf;
                try {
                    buf = questionService.getOne(question);
                }catch (Exception e){
                    buf = null;
                }
                if(buf == null){
                    buf = new Question(question,da);
                    map.put("code","1");
                }else{
                    buf.setDa(da);
                    map.put("code","2");
                }
                buf = questionService.save(buf);
                map.put("status","200");
                map.put("data",buf);
            }
            session.invalidate();
            return map;
        }
        Question q = new Question(question,da);
        q = questionService.save(q);
        if(q == null){
            map.put("status", "500");
            map.put("msg","保存失败");
            return map;
        }

        map.put("status", "200");
        map.put("code","1");
        map.put("data",q);
        session.invalidate();
        return map;
    }

}
