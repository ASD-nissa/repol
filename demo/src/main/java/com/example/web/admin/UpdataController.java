package com.example.web.admin;

import com.example.service.BlogService;
import com.example.vo.ReturnUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;


@Controller
@RequestMapping("/admin")
public class UpdataController {


    @PostMapping("/upload")
    @ResponseBody
    public ReturnUpload updata(@RequestParam("image-file") MultipartFile file,
                               @RequestParam("id") String id
                       ) throws FileNotFoundException {
        String imgName = file.getOriginalFilename();
        String[] imgNames = imgName.split("\\\\");
        imgName = imgNames[imgNames.length-1];
        String path = ResourceUtils.getURL("classpath:").getPath();
        String imgPath= "/static/images/blogImg/" + id;
        File index = new File(path+ imgPath);
        if(!index.exists())
            index.mkdir();

        try{
            String buffPath = imgPath+"/";
            imgPath = buffPath + imgName;
            File imgFile = new File(path + imgPath);
            while(imgFile.exists()){
                imgPath = buffPath+Math.abs(Math.random()*100) + imgName;
                imgFile = new File(path +imgPath);
            }
            file.transferTo(imgFile);

        }catch (Exception e){
            e.printStackTrace();
            return new ReturnUpload("","上传失败",0);
        }
        imgPath.replace("/static","");

        return new ReturnUpload(imgPath,"上传成功",1);
    }
}
