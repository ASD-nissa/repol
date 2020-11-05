package com.example.web.admin;

import com.example.EntityClass.*;
import com.example.service.TagService;
import com.example.utils.CreateNewObject;
import com.example.utils.FIleUtil;
import com.example.vo.BlogQuery;
import com.example.vo.semanticJson;
import com.example.service.BlogService;
import com.example.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/blogs")
public class BlogConteroller {

    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;

    private final String INPUT = "admin/blogs-input";
    private final String LIST = "admin/blogs";
    private final String REDIRECT_LIST = "redirect:/admin/blogs";

    @ResponseBody
    @GetMapping("/json")
    public Object BlogsJson(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                            BlogQuery blog,
                            HttpSession session){
        Map retMap = new HashMap();
        User user = (User) session.getAttribute("user");
        //参数调整
        blog.setTypeId(blog.getTypeId() != null && blog.getTypeId() < 0?null:blog.getTypeId());

        Page<Blog> blogs = blogService.listBlogs(pageable,blog,user);
        retMap.put("blogs", CreateNewObject.CreateNewBlogLit(blogs.getContent()));

        //取出当前页数
        int pageNumber = blogs.getNumber();
        //是否为首页
        boolean isFirst = blogs.isFirst();
        //是否为最后一页
        boolean isLast = blogs.isLast();
        //篇幅是否大于一页

        retMap.put("pageNumber",pageNumber);
        retMap.put("isFirst",isFirst);
        retMap.put("isLast",isLast);

        //获取全部的type
        List<Type> types = typeService.getAll(user);
        retMap.put("types",CreateNewObject.CreateNewTypeList(types));

        return retMap;
    }

    @ResponseBody
    @PostMapping("/json/save")
    public Object saveBlogJson(@RequestParam("file") MultipartFile Picture, @Valid Blog blog, BindingResult result, HttpSession session, RedirectAttributes attributes){
        //设置保存图片的文件夹名称
        blog.setImgName(new Date().getTime()+"");
        saveBlog(Picture,blog,result,session,attributes);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @PostMapping("/json/saveEditor")
    public Object saveEditorJson(@RequestParam("id") Long id,
                                 @RequestParam("file") MultipartFile Picture,
                                 @Valid Blog blog, BindingResult result,
                                 HttpSession session,
                                 RedirectAttributes attributes){
        saveEditor(id,Picture,blog,result,session,attributes);
        return attributes.getFlashAttributes();
    }

    @ResponseBody
    @GetMapping("/json/input")
    public Object editorBlogJson(@RequestParam("id") Long id, HttpSession session){
        Map retMap = new HashMap();

        Blog blog = blogService.getBolgById(id);
        if(blog == null)
            return retMap;
        //初始化blog中的TagIds
        List<Tag> tags = blog.getTags();
        if(tags != null && tags.size()!=0) {
            String TagIds = "";
            for (int i = 0; i < tags.size() - 1; i++)
                TagIds += tags.get(i).getId() + ",";
            TagIds += tags.get(tags.size() - 1).getId();
            blog.setTagIds(TagIds);
        }
        tags = tagService.getAll((User)session.getAttribute("user"));
        //初始化blog中的TagIds完成
        Blog nBlog = CreateNewObject.CreateNewBlog(blog);
        retMap.put("title",nBlog.getTitle());
        retMap.put("flag",nBlog.getFlag());
        retMap.put("imgName",nBlog.getImgName());
        retMap.put("authorDetails",nBlog.getAuthorDetails());
        retMap.put("tagIds",nBlog.getTagIds());
        retMap.put("type",nBlog.getType());
        retMap.put("fristPicture",nBlog.getFristPicture());
        retMap.put("content",nBlog.getContent());
        retMap.put("recommend",nBlog.getRecommend());
        retMap.put("statement",nBlog.getStatement());
        retMap.put("appreciate",nBlog.getAppreciate());
        retMap.put("commentabled",nBlog.getCommentabled());


        return retMap;
    }

    @ResponseBody
    @GetMapping("/json/delete")
    public Object deleteJson(@RequestParam("id") Long id, HttpSession session,RedirectAttributes attributes){
        delete(id,session,attributes);
        return attributes.getFlashAttributes();
    }

    @GetMapping
    public String Blogs(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        model.addAttribute("page",blogService.listBlogs(pageable,blog,user));
        return LIST;
    }

    @GetMapping("/input")
    public String blogsInput(HttpSession session,Model model){
        Blog blog = new Blog();
        blog.initBlog();
        blog.setType(new Type());
        //设置imgName 作为图片保存的文件夹名
        blog.setImgName(new Date().getTime()+"");
        model.addAttribute("blog",blog);
        //初始化Tags
        List<Tag> tags = tagService.getAll((User)session.getAttribute("user"));
        if(tags != null && tags.size() != 0)
            model.addAttribute("tags",tags);
        //初始化TypeS
        List<Type> types = typeService.getAll((User)session.getAttribute("user"));
        if(types != null && types.size() != 0)
            model.addAttribute("types",types);
        return INPUT;
    }

    @GetMapping("/{id}/input")
    public String editorBlog(@PathVariable Long id, HttpSession session,Model model){
        Blog blog = blogService.getBolgById(id);
        if(blog == null)
            return REDIRECT_LIST;
        //初始化blog中的TagIds
        List<Tag> tags = blog.getTags();
        if(tags != null && tags.size()!=0) {
            String TagIds = "";
            for (int i = 0; i < tags.size() - 1; i++)
                TagIds += tags.get(i).getId() + ",";
            TagIds += tags.get(tags.size() - 1).getId();
            blog.setTagIds(TagIds);
        }
        tags = tagService.getAll((User)session.getAttribute("user"));
        //初始化blog中的TagIds完成

        //初始化TypeS
        List<Type> types = typeService.getAll((User)session.getAttribute("user"));
        if(types != null && types.size() != 0)
            model.addAttribute("types",types);
        model.addAttribute("blog",blog);
        model.addAttribute("tags",tags);
        return INPUT;
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,RedirectAttributes attributes){
        User user = (User) session.getAttribute("user");
        boolean b = false;
        Blog blog = blogService.getBolgById(id);
        //imgName 为该博客的图片文件夹名字 路径在/static/images/blogImg/
        String imgName = blog.getImgName();
        String FirstPicture = blog.getFristPicture();
        if(user.getType() == 1 || blogService.getBolgById(id).getUser().getId() == user.getId()) {
            b = blogService.deleteById(id);
        }

        if(b){
            String path = null;
            try {
                path = ResourceUtils.getURL("classpath:").getPath();
                File Imgfile = new File(path+"static/"+FirstPicture);
                if(Imgfile.exists())
                    Imgfile.delete();
                FIleUtil.delete(path+"static/images/blogImg/"+imgName);
            } catch (FileNotFoundException e) {
                attributes.addFlashAttribute("message","删除图片失败 博客删除成功");
                e.printStackTrace();
                return REDIRECT_LIST;
            }
            attributes.addFlashAttribute("message","删除成功");
        }else{
            attributes.addFlashAttribute("message","删除失败");
        }
        return REDIRECT_LIST;
    }


    @PostMapping("/search")
    public String search(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, HttpSession session,Model model){
        User user = (User)session.getAttribute("user");
        model.addAttribute("page",blogService.listBlogs(pageable,blog,user));
        return LIST + "::blogsList";
    }


    @PostMapping
    public String saveBlog(@RequestParam("file") MultipartFile Picture, @Valid Blog blog, BindingResult result, HttpSession session, RedirectAttributes attributes){

        if(result.hasErrors()){
            return INPUT;
        }

        User user = (User)session.getAttribute("user");
        Blog blog1 = blogService.getBolgByTitle(blog.getTitle());
        if( blog1 != null  /*&& blog1.getUser().getId() == user.getId()*/ ){
            result.rejectValue("title","titleError","标题重复");
            return INPUT;
        }

        //取值完整的type tag
        blog.setType(typeService.getTypeById(blog.getType().getId()));
        if(blog.getTagIds() != null && !"".equals(blog.getTagIds()))
            blog.setTags(tagService.ListByIds(blog.getTagIds()));
        blog.setUser((User)session.getAttribute("user"));
        blog.setViews(0);
        //设置日期
        blog.setUpdateTime(new Date());

        //取值imgName 和 ristPicture
        String imgName = Picture.getOriginalFilename();
        String[] imgNames = imgName.split("\\\\");
        imgName = imgNames[imgNames.length-1];
        File imgFile = null;



        try{
            String path = ResourceUtils.getURL("classpath:").getPath();
            imgFile = new File(path+"static/images/"+imgName);
            while(imgFile.exists()){
                imgName = Math.abs(Math.random())*100+imgName;
                imgFile = new File(path+"static/images/"+imgName);
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("message","图片保存失败");
            return REDIRECT_LIST;
        }
        //blog.setImgName(imgName);
        blog.setFristPicture("/images/"+imgName);

        //设置imgName 和 firstPicture 完成

        Blog b = blogService.saveBlog(blog);

        if(b == null){
            attributes.addFlashAttribute("message","新增博客失败");
        }else{
            try {
                Picture.transferTo(imgFile);
            } catch (IOException e) {
                attributes.addFlashAttribute("message","新增博客成功 图片保存失败");
                return REDIRECT_LIST;
            }
            attributes.addFlashAttribute("message","新增博客成功");
        }
        return REDIRECT_LIST;
    }

    @PostMapping("/{id}")
    public String saveEditor(@PathVariable Long id,@RequestParam("file") MultipartFile Picture, @Valid Blog blog, BindingResult result, HttpSession session, RedirectAttributes attributes){
        if(result.hasErrors()){
            return INPUT;
        }

        User user = (User)session.getAttribute("user");
        if(blogService.getBolgById(id).getUser().getId() != user.getId()){
            return REDIRECT_LIST;
        }

        Blog blog1 = blogService.getBolgByTitle(blog.getTitle());
        if( blog1 != null && blog1.getId() != id/*&& blog1.getUser().getId()==user.getId()*/){
            result.rejectValue("title","titleError","标题重复");
            return INPUT;
        }

        //取值完整的type tag
        blog.setType(typeService.getTypeById(blog.getType().getId()));
        if(blog.getTagIds() != null && !"".equals(blog.getTagIds()))
            blog.setTags(tagService.ListByIds(blog.getTagIds()));
        blog.setUser((User)session.getAttribute("user"));
        //设置更新日期
        blog.setUpdateTime(new Date());

        File imgFile = null;
        //修改图片
        if(Picture != null && !"".equals(Picture.getOriginalFilename())){
            //删除原图片
            try {
                String path = ResourceUtils.getURL("classpath:").getPath();
                File OldImgfile = new File(path+"static/"+blog.getFristPicture());
                if(OldImgfile.exists())
                    OldImgfile.delete();
                blog.setFristPicture(null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String imgName = Picture.getOriginalFilename();
            String[] imgNames = imgName.split("\\\\");
            imgName = imgNames[imgNames.length-1];


            try{
                String path = ResourceUtils.getURL("classpath:").getPath();
                imgFile = new File(path+"static/images/"+imgName);
                while(imgFile.exists()){
                    imgName = Math.abs(Math.random())*100+imgName;
                    imgFile = new File(path+"static/images/"+imgName);
                }
                //blog.setImgName(imgName);
                blog.setFristPicture("/images/"+imgName);
            } catch (Exception e) {
                attributes.addFlashAttribute("message", "图片保存失败");
                blogService.updateBlog(blog.getId(),blog);
                return REDIRECT_LIST;
            }

        }//修改图片结束 以及setImgName setFristPicture 完成


        Blog b = blogService.updateBlog(blog.getId(),blog);

        if(b == null){
            attributes.addFlashAttribute("message","修改博客失败");
        }else{
            try {
                if(imgFile != null)
                    Picture.transferTo(imgFile);
            } catch (IOException e) {
                attributes.addFlashAttribute("message","修改博客成功 图片保存失败");
            }
            attributes.addFlashAttribute("message","修改博客成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/json/types")
    @ResponseBody
    public semanticJson getJsonTypes(HttpServletRequest request,HttpSession session){
        User user = (User)session.getAttribute("user");
        List<Type> types = typeService.getAll(user);
        for(Type type :types) {
            type.setBlogs(null);

            User nUser = new User();
            nUser.setUsername(type.getUser().getUsername());
            type.setUser(nUser);
        }
        semanticJson<Type> semanticJson = new semanticJson<>(true,types);
        return semanticJson;
    }

    @GetMapping("/json/tags")
    @ResponseBody
    public semanticJson getJsonTags(HttpServletRequest request,HttpSession session){
        User user = (User)session.getAttribute("user");
        List<Tag> tags = tagService.getAll(user);
        for(Tag tag : tags) {
            tag.setBlogs(null);

            User nUser = new User();
            nUser.setUsername(tag.getUser().getUsername());
            tag.setUser(nUser);
        }
        semanticJson<Tag> semanticJson = new semanticJson<>(true,tags);
        return semanticJson;
    }
}
