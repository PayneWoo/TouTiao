package com.hnust.dao;

import com.hnust.model.Comment;
import com.hnust.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * MessageDAO 用于访问数据库中的message表
 * Created by BigBoss on 2017/6/13.
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";      //访问数据库的 message 表
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FILEDS = " id, " + INSERT_FIELDS;   //要选择出的评论属性

    /**
     *  添加消息 到数据库的 message 表中
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId}) "})
    int addMessage(Message message);        //添加失败会返回 0 ，成功的话，message 的 id 属性会被赋值

    /**
     * 将同一个conversation的消息都读取出来
     */
    @Select({"select ", SELECT_FILEDS, " from ", TABLE_NAME,
            " where conversation_id = #{conversationId} order by id desc limit #{offset}, #{limit}" })
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 会话列表
     * select * from ", TABLE_NAME,"where from_id=#{userId} order by id desc   取得跟当前用户相关的所有消息并降序排序
     * group by conversation_id 然后根据会话Id进行分组，这样就只会取出每个分组中最新的一条消息
     * 然后再根据id对会话进行降序排序
     */
    @Select({"select ", INSERT_FIELDS, " ,count(id) as id " +
            "from ( select * from ", TABLE_NAME,
                    " where from_id=#{userId} or to_id=#{userId} order by id desc) tt" +
                    " group by conversation_id " +
            "order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    /**
     * 获取未读消息数目
     * @param userId：用户ID
     * @param conversationId：会话id
     * @return :未读消息数目
     */
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

}
