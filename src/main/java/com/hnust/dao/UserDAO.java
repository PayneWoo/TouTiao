package com.hnust.dao;

import com.hnust.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {
    //通过注解的方式插入数据
    String TABLE_NAME = "user";
    String INSET_FILEDS = " name, password, salt, head_url ";
    String SELECT_FILEDS = " id, name, password, salt, head_url";
    //@Insert({"insert into user(name, password, salt, head_url) values()"})
    @Insert({"insert into ", TABLE_NAME, "(", INSET_FILEDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"SELECT ", SELECT_FILEDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Select({"SELECT ", SELECT_FILEDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from", TABLE_NAME, " where id = #{id}"})
    void deleteById(int id);
}
