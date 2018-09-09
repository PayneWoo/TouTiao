package com.hnust.service;

import com.hnust.dao.NewsDAO;
import com.hnust.dao.UserDAO;
import com.hnust.model.News;
import com.hnust.model.User;
import com.hnust.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;
    @Autowired
    private UserDAO userDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    /**
     * 上传图片并保存到本地服务器
     * @param file
     * @return 文件的完整保存路径，给前端用
     */
    public String saveImage(MultipartFile file) throws IOException {
        /*首先要判断上传的是否是一张图片，通过判断文件的后缀名来实现*/
        int dotPos = file.getOriginalFilename().lastIndexOf(".");    //获取文件名中最后一个点的位置
        if (dotPos < 0) {   //格式不符合
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();  //文件的后缀名fileExt
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {      //文件格式不符合，不允许上传到本地服务器
            return null;
        }
        //格式符合，则将上传的图片文件保存到本地目录 G:/upload/ 下
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." +fileExt; //给上传的图片文件生成一个随机文件名 fileName
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);       //把上传的图片文件复制到目标目录---- G:/upload/ 下,如果G:/upload/下已存在该文件，则替换
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;       //返回文件的完整保存路径，给前端用
    }

    /**
     * 添加资讯到数据库，并返回资讯id
     * */
    public int addNews(News news) {
        newsDAO.addNews(news);  //添加资讯到数据库
        return news.getId();
    }
    /***
     *根据资讯Id获取资讯
     */
    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    /***
     * 更新评论条数
     */
    public int updateCommentCount(int EntityId, int count) {
        return newsDAO.updateCommentCount(EntityId, count);
    }

    /**
     *根据 userId 获取 user
     */
    public User getUser(int userId) {
        return userDAO.selectById(userId);
    }

    /**
     * 更新点赞数
     * @param id  资讯id
     * @param count     喜欢数
     */
    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);    //调用DAO层来更新点赞数
    }
}
