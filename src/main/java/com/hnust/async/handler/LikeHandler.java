package com.hnust.async.handler;

import com.hnust.async.EventHandler;
import com.hnust.async.EventModel;
import com.hnust.async.EventType;
import com.hnust.model.Message;
import com.hnust.model.User;
import com.hnust.service.MessageService;
import com.hnust.service.UserService;
import com.hnust.async.EventHandler;
import com.hnust.async.EventModel;
import com.hnust.async.EventType;
import com.hnust.model.Message;
import com.hnust.model.User;
import com.hnust.service.MessageService;
import com.hnust.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 关注点赞等行为
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(3);
        message.setToId(model.getEntityOwnerId());
//        message.setToId(model.getActorId());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName() +
                " 赞了你的资讯,http://127.0.0.1:8080/news/"
                + String.valueOf(model.getEntityId()));
        // SYSTEM ACCOUNT
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
//        System.out.println("Liked");
    }

    /**
     * 关注点赞事件，发生点赞事件就要调用 doHandle 方法来处理
     * @return
     */
    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
