package com.example.web.admin;

import com.example.EntityClass.Music;
import com.example.service.MusicService;
import com.example.vo.PageOne;

import com.sun.javafx.fxml.builder.JavaFXFontBuilder;
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


import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/media")
public class MediaController {

    @Autowired
    MusicService musicService;

    //music

    @ResponseBody
    @GetMapping("/music/json")
    public Object musicJson(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable){
        Map retMap = new HashMap();
        Page<Music> musics = musicService.getAll(pageable);
        retMap.put("musics",musics.getContent());

        //取出当前页数
        int pageNumber = musics.getNumber();
        //是否为首页
        boolean isFirst = musics.isFirst();
        //是否为最后一页
        boolean isLast = musics.isLast();

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        return retMap;
    }

    @ResponseBody
    @PostMapping("/music/json/search")
    public Object searchJson(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("name") String name){
        Map retMap = null;
        List<Music> music;
        if(name == null || "".equals(name)){
           retMap = (Map)musicJson(pageable);
           retMap.put("state","0");
        }
        else{
            retMap = new HashMap();
            music = musicService.listByName(name);
            retMap.put("state","1");
            retMap.put("musics",music);
        }
       return retMap;
    }

    @ResponseBody
    @GetMapping("/music/json/delete")
    public Object musicDeleteJson(@RequestParam("id") Long id) throws FileNotFoundException{
        Map retMap = new HashMap();
        musicDelete(id);
        if(musicService.getOne(id)==null)
            retMap.put("message","删除成功");
        else
            retMap.put("message","删除失败");
        return retMap;
    }

    @GetMapping("/music")
    public String music(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable, Model model){
        model.addAttribute("page",musicService.getAll(pageable));
        return "admin/music";
    }

    @PostMapping("/music/search")
    public String search(@RequestParam("name") String name,Model model){
        List<Music> music;
        if(name == null || "".equals(name))
            music = new ArrayList<>(0);
        else
            music = musicService.listByName(name);
        Page<Music> page = new PageOne<Music>(music);
        model.addAttribute("page",page);
        return "admin/music::tableContainer";
    }

    @GetMapping("/music/input")
    public String inputMusic(){
        return "admin/music-input";
    }

    @PostMapping("/music/update")
    @ResponseBody
    public List<Map> updateMusic(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<Map> maps = new ArrayList<>();

        String filePath = new String();
        try {
            filePath = ResourceUtils.getURL("classpath:").getPath() + "static/media/music/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(MultipartFile file : files) {
            Map<String, String> map = new HashMap<>();
            String fileName = file.getOriginalFilename();
            filePath += fileName;
            File musicFIle = new File(filePath);
            if (musicFIle.exists()) {
                map.put("code", "300");
                map.put("redirect", "/admin/media/music/input");
                map.put("msg", "文件已存在 重定向");
                maps.add(map);
            }
            try {
                file.transferTo(musicFIle);
            } catch (IOException e) {
                if (!musicFIle.getParentFile().exists()) {
                    musicFIle.getParentFile().mkdirs();
                }
                file.transferTo(musicFIle);
            }

            Music music = new Music(fileName, "/media/music/" + fileName);
            music = musicService.save(music);

            if (music == null) {
                map.put("code", "500");
                map.put("redirect", "/admin/media/music/input");
                musicFIle.delete();
                map.put("msg", "数据库更新保存失败 重定向");
                maps.add(map);
            }
            if(map.get("code")==null)
                map.put("code","200");
            maps.add(map);
        }

        return maps;
    }

    @GetMapping("/music/{id}/delete")
    public String musicDelete(@PathVariable Long id) throws FileNotFoundException {
        String classpath = ResourceUtils.getURL("classpath:").getPath();
        Music music = musicService.getOne(id);
        File musicFile = new File(classpath + "static/"+music.getPath());

        if(musicFile.exists())
            musicFile.delete();
        musicService.delete(id);

        return "redirect:/admin/media/music";
    }

}
