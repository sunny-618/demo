//package com.glz.spring.helloworld.demo;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ChatDemo implements Demo{
//
//    @Autowired
//    private ChatClient chatClient;
//
//    @Override
//    public String title() {
//        return "基础对话（ChatClient 同步调用）";
//    }
//
//    @Override
//    public int order() {
//        return 1;
//    }
//
//    @Override
//    public void run() throws Exception {
//        String question = "用一句话介绍一下Spring AI是什么。";
//        System.out.println("问：" + question);
//        String answer  = chatClient.prompt()
//                .user(question)
//                .call()
//                .content();
//        System.out.println("答：" + answer);
//    }
//}
