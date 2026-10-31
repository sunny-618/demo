package com.glz.spring.helloworld.controller;

import com.glz.spring.helloworld.bean.FeedbackAnalysis;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MyAIController {

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/prompts/intro-template.st")
    private Resource introTemplate;

    @Value("classpath:/prompts/system-template.st")
    private Resource systemTemplate;

    @GetMapping("/test")
    public String test(@RequestParam String topic,@RequestParam String style){
        PromptTemplate template = new PromptTemplate(introTemplate);
        Prompt prompt = template.create(Map.of("style",style,"topic",topic));
        return chatClient.prompt(prompt)
                .system("你是一个科普作者，回答必须控制在50字以内。")
                .call()
                .content();
    }

    @GetMapping("/test1")
    public String test1(@RequestParam String topic){
        ChatOptions.Builder builder = ChatOptions.builder()
                .temperature(0.3)
                .maxTokens(100);

        return chatClient.prompt()
                .system("你是一个科普作者。")
                .user("介绍一下" + topic)
                .options(builder)
                .call()
                .content();
    }

    @GetMapping("/test2")
    public String test2(@RequestParam String topic, @RequestParam String style){
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemTemplate);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("profession","Java","skill","后端开发"));

        PromptTemplate template = new PromptTemplate(introTemplate);
        Prompt prompt = template.create(Map.of("topic",topic,"style",style));
        prompt.augmentSystemMessage(systemMessage.getText());

        ChatOptions.Builder builder = ChatOptions.builder()
                .temperature(0.3)
                .maxTokens(100);

        return chatClient.prompt(prompt)
                .options(builder)
                .call()
                .content();
    }


    @GetMapping("/analyze")
    public FeedbackAnalysis test3(@RequestParam String feedback){
        return chatClient.prompt()
                .system("你是一个用户反馈分析器。分析用户对 Java 版本的反馈，提取情感倾向、关键词和总结。")
                .user("随便说说" + feedback)
                .call()
                .entity(FeedbackAnalysis.class, spec -> spec
                        .validateSchema() //校验失效时自校正重试
                        .useProviderStructuredOutput() // 请求侧约束（如果你的模型支持）
                );
    }

    @GetMapping("/demo")
    public String demo(){
        String question = "用一句话介绍一下Spring Ai是什么。";
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();
       return answer;
    }
}
