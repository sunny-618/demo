package com.glz.spring.helloworld.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model){
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .maxMessages(10)
                    .build();
        return ChatClient
                .builder(model) // 创建client对象，以及设置模型为model
//                .defaultAdvisors(new SimpleLoggerAdvisor(),//添加一个日志拦截器
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())// 添加一个聊天记录拦截器
//                )
                .build(); // 构建CHatClient对象
    }

    @Bean
    public ChatClient chatOllamaClient(OllamaChatModel model){
        return ChatClient
                .builder(model) // 创建client对象，以及设置模型为model
                .build(); // 构建CHatClient对象
    }

    @Bean
    public ChatClient chatClient2(OpenAiChatModel model){
        //System Prompt工程：清洗语义AI的角色、任务、约束和输出格式
        String systemPrompt = """
                你是一个资深的Java技术顾问。
                禁止回答任何非技术类问题，例如天气或娱乐八卦。
                代码示例必须符合Java 17+规范。
                回答需要符合以下格式：首先一句话概括问题的核心，然后提供代码示例，最后补充注意事项。
                如果自己不确定，可以说“关于这个问题，我目前没有确切的信息”，禁止编造内容。
   
                """;
        return ChatClient
                .builder(model) // 创建client对象，以及设置模型为model
                .defaultSystem(systemPrompt)
//                .defaultAdvisors(new SimpleLoggerAdvisor()) //添加一个日志拦截器
                .build(); // 构建CHatClient对象
    }

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(new InMemoryChatMemoryRepository()) // 内存存储
                .build();
    }
}
