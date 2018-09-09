package com.hnust;

import com.hnust.dao.CommentDAO;
import com.hnust.dao.LoginTicketDAO;
import com.hnust.dao.NewsDAO;
import com.hnust.dao.UserDAO;
import com.hnust.model.*;
import com.hnust.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTests {
    @Autowired
    JedisAdapter jedisAdapter;

	@Test
	public void testObject() {
        User user = new User();
        user.setHeadUrl("http://images.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("def");
        jedisAdapter.setObject("user1", user);      //将用户存到 redis 里

        User u = jedisAdapter.getObject("user1", User.class);   //从 redis 里取出用户
        System.out.print(ToStringBuilder.reflectionToString(u));    //打印用户

	}

}
