package com.hnust.dao;

import com.hnust.model.LoginTicket;
import com.hnust.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {
    //通过注解的方式插入数据
    String TABLE_NAME = "login_ticket";
    String INSERT_FILEDS = " user_id, ticket, expired, status ";
    String SELECT_FILEDS = " id, " + INSERT_FILEDS;

    //@Insert({"insert into user(name, password, salt, head_url) values()"})
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FILEDS,
            ") values (#{userId},#{ticket},#{expired},#{status})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"SELECT ", SELECT_FILEDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
