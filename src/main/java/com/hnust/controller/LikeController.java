package com.hnust.controller;

import com.hnust.async.EventModel;
import com.hnust.async.EventType;
import com.hnust.async.EventProducer;
import com.hnust.model.EntityType;
import com.hnust.model.HostHolder;
import com.hnust.model.News;
import com.hnust.service.LikeService;
import com.hnust.service.NewsService;
import com.hnust.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by BigBoss on 2017/6/19.
 */
@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();      //获取用户id
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);      //喜欢数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);     //更新喜欢数
        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
        .setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount)); //将喜欢数返回到前端
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String disLike(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();      //获取用户id
        long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);      //喜欢数
        newsService.updateLikeCount(newsId, (int) likeCount);     //更新喜欢数
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount)); //将喜欢数返回到前端
    }
}
