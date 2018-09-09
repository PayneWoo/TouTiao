package com.hnust.service;

import com.hnust.dao.LoginTicketDAO;
import com.hnust.dao.UserDAO;
import com.hnust.model.LoginTicket;
import com.hnust.model.User;
import com.hnust.util.ToutiaoUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by BigBoss on 2017/5/31.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    /**
        用户注册
     */
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {    //判断输入的用户名是否为空
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {    //判断输入的密码是否为空
            map.put("msgpassword", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null) { //输入的用户名已被注册
            map.put("msgname", "用户名已被注册!!!");
            return map;
        }
        //输入合法，可以注册
        user = new User();      // 创建一个新用户
        user.setName(username); //设置用户名
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));     //生成盐
        //生成用户头像
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt())); // 设置带盐密码，并用MD5加密
        userDAO.addUser(user);

        //登录
        String ticket = addLoginTicket(user.getId());       //根据用户 id 生成一个特有的 ticket
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {    //判断输入的用户名是否为空
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {    //判断输入的密码是否为空
            map.put("msgpassword", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null) { //输入的用户名已被注册
            map.put("msgname", "用户名不存在");
            return map;
        }
        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpassword", "密码错误");
            return map;
        }
        //输入的用户名和密码正确，服务器给登陆的用户下发一个ticket
        String ticket = addLoginTicket(user.getId());       //根据用户 id 生成一个特有的 ticket
        map.put("ticket", ticket);
        return map;
    }

    /**
     *  为用户设置 一个 ticket
     */
    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();     //生成一个 ticket
        ticket.setUserId(userId);       // 为 ticket 绑定用户
        Date date = new Date(); //当前时间
        date.setTime(date.getTime() + 1000 * 24 * 3600);
        ticket.setExpired(date);    //设置 ticket 过期时间为 24 小时
        ticket.setStatus(0);    //设置 ticket 状态码
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", "")); //设置 ticket
        loginTicketDAO.addTicket(ticket);   //将生成的 ticket 添加到数据库login_ticket 表中去
        return ticket.getTicket();      //返回生成的 ticket
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    /**
     * 登出
     * */
    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);  //设置当前 ticket 状态为：过期

    }
}
