package com.hnust.service;

import com.hnust.dao.CommentDAO;
import com.hnust.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    /**
     * 根据实体获取评论集合
     * @param entityId  ：实体id
     * @param entityType : 实体类型
     * @return
     */
    public List<Comment> getCommentByEntity(int entityId, int entityType) {
        return commentDAO.selectByEntity(entityId, entityType);     //返回当前实体的评论集合
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    public int addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }

    /**
     * 获取评论总数
     * @param entityId
     * @param entityType
     * @return
     */
    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    /**
     * 删除评论
     * @param entityId
     * @param entityType
     */
    public void deleteComment(int entityId, int entityType) {
        commentDAO.updateStatus(entityId, entityType,1);
    }

}
