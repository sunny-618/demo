package com.glz.spring.helloworld.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.glz.spring.helloworld.tool.ScriptTool;
import com.glz.spring.helloworld.tool.StoryboardTool;
import com.glz.spring.helloworld.tool.VideoSynthesisTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;

@Configuration
public class AgentConfig {

    @Bean
    public CompiledGraph videoPipelineGraph(ChatClient chatClient, ScriptTool scriptTool, StoryboardTool storyboardTool, VideoSynthesisTool videoSynthesisTool) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = ()->{
            Map<String, KeyStrategy> strategyMap = new HashMap<>();
            strategyMap.put("plan_output", new ReplaceStrategy());
            strategyMap.put("script_output", new ReplaceStrategy());
            strategyMap.put("storyboard_output", new ReplaceStrategy());
            strategyMap.put("title_tags_output", new ReplaceStrategy());
            strategyMap.put("synthesis_output", new ReplaceStrategy());
            return  strategyMap;
        };
        AsyncNodeAction planNode = AsyncNodeAction.node_async(state -> {
            return Map.of("plan_output","...");
        });

        AsyncNodeAction scriptNode = AsyncNodeAction.node_async(state -> {
            //拿到真正的数据（假设前面节点生成的真正数据叫plan——output）
            String plan = state.value("plan_output").orElse("穿搭").toString();
            String userId = (String) state.value("userId").orElse("defaultUser");
            String scriptResult = chatClient.prompt("请根据以下大纲写个脚本" + plan)
                    .tools(scriptTool)
                   .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                   .call()
                    .content();
            return Map.of("script_output",scriptResult);
        });

        AsyncNodeAction storyboardNode = AsyncNodeAction.node_async(state -> {
            return Map.of("storyboardNode", "...");
        });
        var titleTagsNode = AsyncNodeAction.node_async(state -> {
            return Map.of("title_tags_output", "...");
        });

        var synthesisNode = AsyncNodeAction.node_async(state -> {
            return Map.of("synthesis_output", "...");
        });

        StateGraph graph = new StateGraph(keyStrategyFactory)
                .addNode("plan",planNode)
                .addNode("script",scriptNode)
                .addNode("storyboard",storyboardNode)
                .addNode("title_tags",titleTagsNode)
                .addNode("synthesis",synthesisNode)
                .addEdge(START, "plan")
                .addEdge("plan", "script")
                .addEdge("script", "storyboard")
                .addEdge("storyboard", "title_tags")
                .addEdge("title_tags", "synthesis")
                .addEdge("synthesis", END);
        return graph.compile();
    }
}
