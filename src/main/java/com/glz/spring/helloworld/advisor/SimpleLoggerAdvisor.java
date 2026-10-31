package com.glz.spring.helloworld.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

public class SimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        System.out.println("发送请求前：" + request);
        ChatClientResponse response = chain.nextCall(request);
        System.out.println("接受到响应： " + response);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        System.out.println("发送请求前：" + request);
        return chain.nextStream(request).doOnNext(response ->System.out.println("收到流式响应片段: " + response));
    }

    @Override
    public String getName() {
        return "简单日志Advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
