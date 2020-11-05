package com.example.web;

import com.example.EntityClass.*;
import com.example.EntityClass.superstar.Question;
import com.example.service.*;
import com.example.utils.CreateNewObject;
import com.example.utils.MarkdownToHtmlUtil;
import com.example.vo.jsonMusic;
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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Indexcontroller {

    @Autowired
    private TypeService typeService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MusicService musicService;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){

        model.addAttribute("page",blogService.listBlogs(pageable));
        model.addAttribute("types",typeService.listTop(6));
        model.addAttribute("tags",tagService.listTop(10));
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8));


        return "index";
    }

    @ResponseBody
    @GetMapping("/json/index")
    public Object indexJson(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable){
        HashMap retMap = new HashMap();

        //blog
        Page<Blog> blogs = blogService.listBlogs(pageable);
        //取出当前页数
        int pageNumber = blogs.getNumber();
        //是否为首页
        boolean isFirst = blogs.isFirst();
        //是否为最后一页
        boolean isLast = blogs.isLast();
        //篇幅是否大于一页
        boolean isPages = blogs.getTotalPages()>1;


        if(pageable.getPageNumber()==0) {
            //type
            List<Type> types = typeService.listTop(6);
            List<Map> typeList = new ArrayList<>();
            for (Type old : types) {
                Map nType = new HashMap();
                nType.put("name", old.getName());
                nType.put("id", old.getId());
                nType.put("size", old.getBlogs().size());
                typeList.add(nType);
            }

            //tag
            List<Tag> tags = tagService.listTop(10);
            List<Map> tagList = new ArrayList<>();
            for (Tag old : tags) {
                Map nTag = new HashMap();
                nTag.put("name", old.getName());
                nTag.put("id", old.getId());
                nTag.put("size", old.getBlogs().size());
                tagList.add(nTag);
            }

            //Recommend
            List<Blog> RecommendBlogs = blogService.listRecommendBlogTop(8);
            List<Map> RecommendBlogList = new ArrayList<>();
            for (Blog old : RecommendBlogs) {
                Map n = new HashMap();
                n.put("title", old.getTitle());
                n.put("id", old.getId());
                RecommendBlogList.add(n);
            }
            retMap.put("type",typeList);
            retMap.put("tag",tagList);
            retMap.put("recommend",RecommendBlogList);
        }

        retMap.put("blog",CreateNewObject.CreateNewBlogLit(blogs.getContent()));
        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);
        retMap.put("isPages",isPages);

        return retMap;
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable("id") Long id,Model model){
        Blog blog = blogService.getBolgById(id);
        if(!blog.getState())
            return "redirect:/";
        //浏览次数加以
        blog.setViews(blog.getViews()+1);
        //更新浏览次数到数据库
        blogService.updateBlog(blog.getId(),blog);
        blog.setContent(MarkdownToHtmlUtil.markdownToHtmlExtensions(blog.getContent()));
        model.addAttribute("blog",blog);
        model.addAttribute("comments",commentService.getAllByblogIdAndNoSon(id));

        return "blog";
    }

    @ResponseBody
    @GetMapping("/json/blog")
    public Object blogJson(@RequestParam("id") Long id){
        Blog blog = blogService.getBolgById(id);
        Map retMap = new HashMap();
        if(!blog.getState())
            return null;
        //浏览次数加以
        blog.setViews(blog.getViews()+1);
        //更新浏览次数到数据库
        blogService.updateBlog(blog.getId(),blog);
        blog.setContent(MarkdownToHtmlUtil.markdownToHtmlExtensions(blog.getContent()));

        //comments
        List<Comment> comments=commentService.getAllByblogIdAndNoSon(id);

        retMap.put("blog", CreateNewObject.CreateNewBlog(blog));
        retMap.put("comments",CreateNewObject.CreateNewCommentList(comments));
        return retMap;
    }


    @GetMapping("/searh")
    public String Getsearh(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                           @RequestParam String query,Model model){
        return Postsearh(pageable,query,model);
    }
    @PostMapping("/searh")
    public String Postsearh(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam String query,Model model){
        model.addAttribute("page",blogService.listBlogsByQuery(query,pageable));
        model.addAttribute("query",query);
        return "searh";
    }

    @ResponseBody
    @GetMapping("/search/json")
    public Object GetSearchJson(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestParam("query") String query){
        return PostSearchJson(pageable,query);
    }

    @ResponseBody
    @PostMapping("/search/json")
    public Object PostSearchJson(@PageableDefault(size = 8,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                                 @RequestParam("query") String query){
        Map retMap = new HashMap();
        Page<Blog> blogs = blogService.listBlogsByQuery(query,pageable);
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
        return retMap;
    }

    //超星
    @GetMapping("/superStar")
    public String superStar(){
        return "superStar";
    }

    @GetMapping("/json/music/list")
    @ResponseBody
    public List<jsonMusic> jsonMusicList(HttpSession session){
        Map<String,Object> sessionMusicMap = (Map)session.getAttribute("music");
        sessionMusicMap=sessionMusicMap==null?new HashMap<>():sessionMusicMap;
        List sessionMusic = (List)sessionMusicMap.get("list");
        Long maxId = (Long)sessionMusicMap.get("maxId");
        if(sessionMusic != null && maxId == musicService.getMaxId()){
            return sessionMusic;
        }


        List<jsonMusic> muList= new ArrayList<>();
        List<Music> musics = musicService.getAll();
        maxId = musicService.getMaxId();
        for(Music music : musics){
            muList.add(new jsonMusic(music.getName(),music.getPath()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("list",muList);
        map.put("maxId",maxId);
        session.setAttribute("music",map);
        return muList;
    }

    @GetMapping("/json/superStar")
    @ResponseBody
    public Map jsonSuperStar(@RequestParam("t") String t){
        List<Question> questionList = questionService.likeList(t);
        Map map = new HashMap();
        if(questionList.size()>0){
            map.put("status","200");
            map.put("size",questionList.size());
            map.put("data",questionList);
        }else{
            map.put("status","404");
            map.put("size",0);
        }
        return map;
    }

    @GetMapping("/json/getUser")
    @ResponseBody
    public Object jsonUser(HttpSession session){
        Map retMap = new HashMap();
        User user = (User) session.getAttribute("user");
        if(user != null){
            retMap.put("id",user.getId());
            retMap.put("img",user.getImg());
            retMap.put("nickname",user.getNickname());
            retMap.put("type",user.getType());
            retMap.put("email",user.getEmail());
        }

        return retMap;
    }

}
