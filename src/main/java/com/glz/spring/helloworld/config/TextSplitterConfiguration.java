package com.glz.spring.helloworld.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextSplitterConfiguration {

    @Bean
    public TokenTextSplitter tokenTextSplitter(){
        return TokenTextSplitter.builder()
                .withChunkSize(50) //单个文本块的最大token数
                .withMaxNumChunks(1000) //最大可生成的块数量，防止对超长文本无限分割，达到此上限后即使还有剩余文本也停止生产新块
                .build();
    }
}
