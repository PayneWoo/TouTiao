package com.hnust.model;

import org.springframework.stereotype.Component;

/**
 * 该类用于表示当前的用户是谁
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>(); //线程本地变量，保证了各个线程只能访问自己保存的用户

    public User getUser() {             //获取用户
        return users.get();
    }

    public void setUser(User user) {    //保存用户
        users.set(user);
    }

    public void clear() {               //删除用户
        users.remove();
    }
}
