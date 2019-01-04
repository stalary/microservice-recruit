package com.stalary.pf.outside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * MailService
 *
 * @author lirongqian
 * @since 2018/04/27
 */
@Service
@Slf4j
@RefreshScope
public class MailService {

    private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.password}")
    private String psw;

    @Value("${spring.mail.host}")
    private String host;

    public void sendEmail(String to, String subject, String message) {
        try {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            //设置邮件会话参数
            Properties props = new Properties();
            //邮箱的发送服务器地址
            props.setProperty("mail.smtp.host", host);
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            //邮箱发送服务器端口,这里设置为465端口
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.auth", "true");
            final String username = from;
            final String password = psw;
            //获取到邮箱会话,利用匿名内部类的方式,将发送者邮箱用户名和密码授权给jvm
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            //通过会话,得到一个邮件,用于发送
            Message msg = new MimeMessage(session);
            //设置发件人
            msg.setFrom(new InternetAddress(username));
            //设置收件人,to为收件人,cc为抄送,bcc为密送
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject);
            //设置邮件消息
            msg.setText(message);
            //设置发送的日期
            msg.setSentDate(new Date());
            //调用Transport的send方法去发送邮件
            Transport.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
