package com.hnust.service;

import com.hnust.dao.MessageDAO;
import com.hnust.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    /**
     * 通过调用DAO层来实现添加消息
     */
    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    /**
     * 将同一个conversation的消息都读取出来
     * @param conversationId ：会话id
     * @param offset ：分页数
     * @param limit ：每页大小
     * @return
     */
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    /**
     * 获取会话列表
     * @param userId：用户id
     * @param offset: 页数
     * @param limit： 每页大小
     * @return ：会话列表
     */
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    /**
     * 获取未读消息数目
     * @param userId：用户id
     * @param conversationId:会话id
     * @return : 未读消息数目
     */
    public int getConvesationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConvesationUnreadCount(userId, conversationId);
    }
}
