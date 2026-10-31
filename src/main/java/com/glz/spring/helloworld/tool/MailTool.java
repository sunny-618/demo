package com.glz.spring.helloworld.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailTool {

    @Autowired
    private   JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Tool(description = "给用户发送邮件。参数包含收件人邮箱、邮件标题、邮件正文。")
    public String sendEmail(String email, String title, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject(title);
            message.setText(content);
            mailSender.send(message);
            return "邮件发送成功";
        }catch (Exception e){
            return "邮件发送失败：" + e.getMessage();
        }

    }
}
