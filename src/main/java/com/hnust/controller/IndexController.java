package com.hnust.controller;

import com.hnust.aspect.LogAspect;
import com.hnust.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

import com.hnust.model.User;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("Visit Index");
        return "hello hnust" + session.getAttribute("msg") + "<br> Say:" + toutiaoService.say();
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "hnust") String key) {

        return String.format("GID{%s},  UID{%d}, TYPE{%d}，KEY{%s}",groupId, userId, type, key);
    }

    @RequestMapping(value = {"/vm"})
    public String news(Model model) {   //Model是一个存储数据的模型
        model.addAttribute("value1","value");
        List<String> colors = Arrays.asList(new String[] {"RED", "BLUE", "GREEN"});
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i*i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("bigboss"));
        return "news";   //返回模板文件new.vm
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        StringBuilder sb = new StringBuilder();
        //实现 Enumeration 接口的对象，它生成一系列元素，一次生成一个。连续调用 nextElement 方法将返回一系列的连续元素。
        Enumeration<String> headerNames =  request.getHeaderNames();
        while(headerNames.hasMoreElements()) {  //测试此枚举是否包含更多的元素
            String name = headerNames.nextElement();    //如果此枚举对象至少还有一个可提供的元素，则返回此枚举的下一个元素
            /**
             * 在 StringBuilder 上的主要操作是 append 和 insert 方法，可重载这些方法，
             * 以接受任意类型的数据。每个方法都能有效地将给定的数据转换成字符串，然后
             * 将该字符串的字符追加或插入到字符串生成器中。append 方法始终将这些字符
             * 添加到生成器的末端；而 insert 方法则在指定的点添加字符。
             */
            sb.append(name + ":" + request.getHeader(name) + "<br>");    //将指定的字符串name追加到此字符序列
        }
        for (Cookie cookie : request.getCookies()) {
            sb.append("Cookie");
            sb.append(":");
            sb.append(cookie.getName());
            sb.append("=");
            sb.append(  cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMethod:" + request.getMethod() + "<br>");  //获取request的方法
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");
        return sb.toString();
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value="hnust", defaultValue = "a") String hnust,
                            @RequestParam(value="key", defaultValue="key") String key,
                            @RequestParam(value="value", defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "HNUST From Cookie:" + hnust;
    }


    /**
     * 重定向
     * @param code
     * @return
     */
    @RequestMapping({"/redirect/{code}"})
    public String redirect(@PathVariable("code") int code, HttpSession session) {
        /*
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);  //设置为永久性转移
         }
        return red;
        */
        session.setAttribute("msg", "  hello bigboss . I am jump from redirect");
        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key) {
        if ("admin".equals(key)) {
            return "hello admin";
        }
        throw new IllegalArgumentException("Key错误");    //抛出异常
    }


    /**
     * 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }

}
