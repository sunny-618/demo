package com.glz.spring.helloworld.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.glz.spring.helloworld.agent.MarketingVideoAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/agent/video")
public class VideoAgentController {

    @Autowired
    private MarketingVideoAgent agent;

    @Autowired
    private CompiledGraph videoPipelineGraph;

    @Autowired
    ApplicationContext applicationContext; // 这是 Spring 的大管家

    @GetMapping("/chat")
    public String chat(@RequestParam String topic){
        String[] beans = applicationContext.getBeanNamesForType(ChatModel.class);
        System.out.println(Arrays.toString(beans));
        return  agent.generate("1",topic,"抖音", "专业但轻松");
    }

    @PostMapping("/pipeline")
    public Map<String,Object> runPipeline(@RequestBody Map<String,String> input){
        Map<String,Object> state = new HashMap<>();
        state.put("topic",input.getOrDefault("topic","AI工具提效"));
        state.put("userId",input.getOrDefault("userId", "1"));
        Optional<OverAllState> result = videoPipelineGraph.invoke(state);
        return result.map(OverAllState::data).orElse(new HashMap<>());
    }

}
