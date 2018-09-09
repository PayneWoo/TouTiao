package com.hnust.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.internet.MimeUtility;

import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;
    private Session session;

    @Autowired
    private VelocityEngine velocityEngine;

    /**
     *
     * @param to    邮件接收方
     * @param subject      邮件主题
     * @param template     邮件模板
     * @param model
     * @return
     */
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("H N U S T");          //发件方昵称
            InternetAddress from = new InternetAddress(nick + "<664364583@qq.com>");     //邮件发送方地址
            MimeMessage mimeMessage = mailSender.createMimeMessage();       //构造邮件
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);         //多媒体消息
            //利用Velocity引擎把模板和参数等渲染出来（根据指定模板发送邮件）
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(to);     //邮件接收方
            mimeMessageHelper.setFrom(from);            //邮件发送发
            mimeMessageHelper.setSubject(subject);                  //设置主题
//            mimeMessageHelper.setSentDate(new Date());              //设置邮件发送日期
            mimeMessageHelper.setText(result, true);        //设置邮件内容
            mailSender.send(mimeMessage);                   //发送邮件
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("664364583@qq.com");
        mailSender.setPassword("weuumcqyibocbccd");
        mailSender.setHost("smtp.qq.com");             //设置服务器地址
        mailSender.setPort(465);
        mailSender.setProtocol("smtp");               //设置 SMTP 协议
        mailSender.setDefaultEncoding("utf8");
        /*利用Properties对象获得了邮件发送服务器、接收邮件协议、发送邮
        件协议、用户名、密码等整个应用程序都要使用到的共享信息*/
        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(properties);
    }
}
