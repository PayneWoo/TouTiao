package com.hnust.controller;

import com.hnust.aspect.LogAspect;
import com.hnust.dao.CommentDAO;
import com.hnust.model.*;
import com.hnust.service.*;
import com.hnust.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;      //表示当前用户是否登录

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    SensitiveService sensitiveService;

    /**
     * 资讯详情页面 & 评论功能
     */
    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        News news = newsService.getById(newsId);    //根据资讯Id获取资讯

        if (news != null) { //如果获取到了资讯
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) { //当前用户为登录态
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }
            //根据实体找出所有的评论放入集合中
            List<Comment> comments = commentService.getCommentByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<ViewObject>();  //viewObject用于将评论以及头像等信息显示到页面上
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);     //将评论放入viewObject 里面
                vo.set("user", userService.getUser(comment.getUserId()));   //将相关的用户放入viewObject里
                commentVOs.add(vo); //将viewObject 添加到 commentVOs 集合中，然后我们就可以通过模板文件来显示评论、用户这些信息
            }
            model.addAttribute("comments", commentVOs);
        }
        //没有获取到资讯
        model.addAttribute("news", news);   //没有获取到资讯则添加资讯
        model.addAttribute("owner", newsService.getUser(news.getUserId()));
        return "detail";        //跳转至 detail.html
    }

    /**
     * 增加评论
     */
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            //新增 content 过滤功能
            Comment comment = new Comment();
            comment.setContent(HtmlUtils.htmlEscape(content));      //过滤评论内容中的Html语句（通过对Html语句进行转义）
            comment.setContent(sensitiveService.filter(comment.getContent()));  //过滤评论内容中的敏感词
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            if (content.length() != 0) {    //如果评论内容不为空
                commentService.addComment(comment);
                //更新news里的评论数量
                int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
                newsService.updateCommentCount(comment.getEntityId(), count);
            }
        } catch (Exception e){
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }

    /**
     * 读取图片
     */
    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody       //图片本身是二进制流
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {  //返回图片给前端页面
        try {
            response.setContentType("image/jpeg");  //设置服务器返回内容的类型
            /*服务器将浏览器请求的图片下发给浏览器（服务器将图片保存在pathname),如何下发？服务器是将图片文件写到 response 二进制流里，
            然后浏览器通过 HTTP 协议就可以解析出图片*/
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)),
                    response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + e.getMessage());
        }
    }

    /**
     * 上传图片
     */
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {//通过参数 file 来指定图片的路径，通过二进制流上传图片
        //@RequestParam自动将二进制流保存到 MultipartFile 类型的变量 file 中去
        try {
//            String fileUrl = newsService.saveImage(file);   //saveImage函数用来：上传图片并保存到本地服务器,返回图片的完整保存路径。
            String fileUrl= qiniuService.saveImage(file);   //上传图片到七牛云
            if (fileUrl == null) {  //如果上传的图片的保存路径为空，则说明图片上传失败
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);   //图片上传成功，返回正常的 code:0 ，和图片的完整保存路径
        } catch (Exception e) {
            logger.error("图片上传失败" + e.getMessage());     //打印日志信息
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    /**
     * 发布资讯
     */
    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String  addNews(@RequestParam("image") String image,
                           @RequestParam("title") String title,
                           @RequestParam("link") String link) {
        try {
            News news = new News();
            if (hostHolder.getUser() != null) { //如果是登录的
                news.setUserId(hostHolder.getUser().getId());   //给资讯设置用户Id
            } else {    //如果没有登录
                //设置一个匿名Id
                news.setUserId(999999);
            }
            news.setImage(image);       //给资讯设置图片
            news.setCreatedDate(new Date());    //给资讯设置创建日期
            news.setTitle(HtmlUtils.htmlEscape(title));       //给资讯设置标题并过滤标题中的Html语句
            news.setLink(HtmlUtils.htmlEscape(link));         //给咨讯设置链接并过滤链接中的Html语句
            news.setTitle(sensitiveService.filter(news.getTitle()));    //过滤标题中的敏感词
            news.setLink(sensitiveService.filter(news.getLink()));      //过滤链接中的敏感词
            newsService.addNews(news);  //添加一条资讯
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }

}
