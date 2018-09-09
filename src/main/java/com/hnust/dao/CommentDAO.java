package com.hnust.dao;

import com.hnust.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用于访问数据库中对应的数据
 */

@Mapper
public interface CommentDAO {

    String TABLE_NAME = "comment ";      //访问数据库的 comment 表
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status";
    String SELECT_FILEDS = " id, " + INSERT_FIELDS;   //要选择出的评论属性

    /**
     * 通过注解的方式 添加评论 到数据库的 comment 表中
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{content}, #{userId}, #{entityId}, #{entityType}, #{createdDate}, #{status}) "})
    int addComment(Comment comment);        //添加失败会返回 0 ，成功的话，comment 的 id 属性会被赋值

    /**
     * 通过注解的方式更新数据库中的评论状态
     */
    @Update({"update ", TABLE_NAME, " set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);

    /**
     * 通过注解的方式从数据库的 comment 表中查询出评论
     */
    @Select({"select ", SELECT_FILEDS, " from ", TABLE_NAME,
            " where entity_id = #{entityId} and entity_type = #{entityType} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    /**
     * 通过注解的方式从数据库中得出评论的数量
     */
    @Select({"select count(id) from ", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType}"})
   int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

}
