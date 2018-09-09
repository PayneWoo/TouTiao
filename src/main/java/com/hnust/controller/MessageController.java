package com.hnust.controller;

import com.hnust.model.*;
import com.hnust.service.MessageService;
import com.hnust.service.UserService;
import com.hnust.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);     //日志

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 站内信列表
     * @param model
     * @return
     */
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String ConversationList(Model model) {
        try {
            //获取当前登录用户的id
            int localUserId = hostHolder.getUser().getId();
            //ViewObject内置了一个 Map ,我们可以往里边放任何数据，方便传递任何数据到Velocity
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            //会话列表
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                //获取收信人id
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }


    /**
     * 显示给定 conversationId 下的所有消息
     * @param model
     * @param conversationId ：会话Id
     * @return
     */
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String ConversationDetail(Model model, @Param("conversationId") String conversationId) {
        try {
            List<Message> conversationList = messageService .getConversationDetail(conversationId, 0, 10);
            //ViewObject内置了一个 Map ,我们可以往里边放任何数据，方便传递任何数据到Velocity
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                //将viewObject 添加到 messages 集合中，然后我们就可以通过模板文件来显示同一个会话id 下的消息
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
            return "letterDetail";
        } catch (Exception e) {
            logger.error("显示消息出错" + e.getMessage());
        }
        return "letterDetail";
    }


    /**
     * 增加消息
     * @param fromId：发信人id
     * @param toId：收信人id
     * @param content: 消息内容
     * @return
     */
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg); //将消息插入到数据库中
            return ToutiaoUtil.getJSONString(msg.getId());  //返回消息id
        } catch (Exception e){
            logger.error("增加消息失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "插入消息失败");
        }

    }
}
