package com.glz.spring.helloworld.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class MarketingVideoAgent {

   private final ChatClient chatClient;


    public MarketingVideoAgent(OpenAiChatModel chatModel){
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
    }

    public String generate(String userId,String topic, String platform, String tone){
        String prompt = """
                你是营销短视频策划助手。
                主题：%s
                平台：%s
                语气：%s
                请先生成脚本，再输出分镜、标题、话题标签和封面文案。
                """.formatted(topic,platform,tone);
        return chatClient.prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .call().content();
    }
}
