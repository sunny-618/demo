package com.glz.spring.helloworld.controller;

import com.glz.spring.helloworld.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class AIChatController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatClient chatClient2;

    @Autowired
    private ChatClient chatOllamaClient;

//    @Autowired
//    private DateTimeTools dateTimeTools;

    @RequestMapping(value = "/aiTest.action", method = RequestMethod.GET)
    public String ai(@RequestParam(value = "question", required = false) String question){
        return chatClient.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
                .call() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/aiTool.action", method = RequestMethod.GET)
    public String aiTool(@RequestParam(value = "question", required = false) String question,@RequestParam(value = "convId", defaultValue = "default") String convId){
        return chatClient.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
//                .advisors(a->a.param  (ChatMemory.CONVERSATION_ID,convId))
//                .tools(dateTimeTools) //注册工具
                .call() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/aiplus.action", method = RequestMethod.GET)
    public String aiplus(@RequestParam(value = "question", required = false) String question,@RequestParam(value = "convId", defaultValue = "default") String convId){
        return chatClient.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
//                .advisors(a->a.param  (ChatMemory.CONVERSATION_ID,convId))
                .call() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/ai2.action", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public Flux<String> ai2(@RequestParam(value = "question", required = false) String question){
        return chatClient.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
                .stream() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/ollama.action", method = RequestMethod.GET)
    public String ollama(@RequestParam(value = "question", required = false) String question){
        return chatOllamaClient .prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
                .call() //调用模型，返回结果
                .content();
    }

    /**
     * 一套模版适用多种场景
     * @param topic
     * @return
     */
    @RequestMapping(value = "/ask.action", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public Flux<String> ask(@RequestParam(value = "topic", required = false) String topic){
        PromptTemplate template = new PromptTemplate("介绍下{topic}");
        Prompt prompt = template.create(Map.of("topic",topic));
        return chatClient.prompt(prompt) //创建Prompt引擎，用于构建聊天请求
                .stream() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/ask2.action", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public Flux<String> ask2(@RequestParam(value = "topic", required = false) String topic){
        PromptTemplate template = new PromptTemplate("介绍下{topic}");
        Prompt prompt = template.create(Map.of("topic",topic));
        return chatClient.prompt(prompt) //创建Prompt引擎，用于构建聊天请求
                .system("你是一个专业的书评助手") // 设置系统提示词
                .user(u->u.text("请给我推荐三本关于{topic}的书籍，主需要返回书名").param("topic",topic))
                .stream() //调用模型，返回结果
                .content();
    }

    @RequestMapping(value = "/ask3.action", method = RequestMethod.GET)
    public String ask3(@RequestParam(value = "question", required = false) String question){
        return chatClient2.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(question) //设置用户输入
                .call() //调用模型，返回结果
                .content();
    }

    public record TopicBook(String topic, List<String> books){}

    @RequestMapping(value = "/ask4.action", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String ask4(@RequestParam(value = "topic", required = false) String topic) {
        PromptTemplate template = new PromptTemplate("介绍下{topic}");
        Prompt prompt = template.create(Map.of("topic", topic));
        TopicBook topicBook = chatClient.prompt(prompt) //创建Prompt引擎，用于构建聊天请求
                .system("你是一个专业的书评助手") // 设置系统提示词
                .user(u -> u.text("请给我推荐三本关于{topic}的书籍，主需要返回书名").param("topic", topic))
                .call().entity(TopicBook.class);
        System.out.println(topicBook);
        return "ok";
    }

    public record BookReview(String reviewerName, int rating, String comment){ }

    @RequestMapping(value = "/ask5.action", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String ask5() {
        List<BookReview> bookReviews = chatClient.prompt() //创建Prompt引擎，用于构建聊天请求
                .user(u -> u.text("请给{bookName}书籍三条评价信息").param("bookName", "Effective Java"))
                .call().entity(new ParameterizedTypeReference<List<BookReview>>() {
                });
        System.out.println(bookReviews);
        return "ok";
    }
}

