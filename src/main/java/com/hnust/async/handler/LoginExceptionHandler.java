package com.hnust.async.handler;

import com.hnust.async.EventHandler;
import com.hnust.async.EventModel;
import com.hnust.async.EventType;
import com.hnust.controller.IndexController;
import com.hnust.model.Message;
import com.hnust.service.MessageService;
import com.hnust.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/14.
 */
@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //判断登录是否异常

        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登陆IP异常");
        // SYSTEM ACCOUNT
        message.setFromId(3);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);

        //构造一个 Map,将username导入模板
        Map<String, Object> map = new HashMap();
        map.put("username", model.getExt("username"));

        //发送邮件
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆异常",
                "mails/welcome.html", map);
    }

    /**
     * 关注的事件
     * @return
     */
    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
