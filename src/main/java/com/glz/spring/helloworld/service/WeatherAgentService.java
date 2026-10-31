package com.glz.spring.helloworld.service;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.glz.spring.helloworld.entity.User;
import com.glz.spring.helloworld.tool.DataBaseQueryTool;
//import com.glz.spring.helloworld.tool.HotSpotTool;
import com.glz.spring.helloworld.tool.MailTool;
import com.glz.spring.helloworld.tool.WeatherTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;

@Slf4j
@Service
public class WeatherAgentService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private WeatherTools weatherTools;

    @Autowired
    private MailTool mailTool;

    @Autowired
    private DataBaseQueryTool dataBaseQueryTool;



    // langchain调用
    public String executeTask(long userId){

        String prompt = """
                你是一个贴心的生活助理。
                当前用户ID是：%s。
                请完成以下任务：
                1. 查询该用户的家庭地址。
                2. 查询该用户的邮箱。
                3. 根据地址查询当地天气。
                4. 给该用户的邮箱发一封邮件，告知他今天的天气，并给出一句穿衣建议。
                """.formatted(userId);
        System.out.println("🚀 [AI 开始思考] 目标已下发...");

        String result = chatClient.prompt(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,userId))
                .tools(weatherTools,mailTool,dataBaseQueryTool)
                .call()
                .content();
        return  result;
    }


    // langGraph调用
    AsyncNodeAction queryAddressNode = AsyncNodeAction.node_async(state -> {
        long userId = (Long) state.value("userId").orElse(0L);
        // 直接调用你的Java方法查库
        User user = dataBaseQueryTool.executeSelectQuery(userId);
        Map<String,Object> updates = new HashMap<>();
        updates.put("userName",user.getName());
        updates.put("email",user.getEmail());
        updates.put("address",user.getAddress());
        log.info("📍 [queryAddressNode] 执行完毕，用户: {}", user);
        return updates;
    });

    AsyncNodeAction hotNode = AsyncNodeAction.node_async(state -> {
        long userId = (Long) state.value("userId").orElse("");
        // 直接调用你的Java方法
//        List<String> result = hotSpotTool.getHotNews();
//        log.info("📍 [hotNode] 执行完毕，结果: {}", result);
        // 这里可以让大模型帮你写一个小故事
        String advice = chatClient.prompt("提醒今天历史上发生过什么事情，或者有哪些历史名人")
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,userId))
                .call().content();
        log.info("📍 [hotNode] 执行完毕，结果: {}", advice);
        return Map.of("essay",advice);
    });

    AsyncNodeAction weatherNode = AsyncNodeAction.node_async(state -> {
        log.info("📍 [weatherNode] 进入节点，当前 State: {}", state.data());
        String city = (String) state.value("address").orElse("");
        log.info("📍 [weatherNode] 准备查询天气，地址: {}", city);
        long userId = (Long) state.value("userId").orElse("");
        String userName = (String) state.value("userName").orElse("");
        String essay = (String) state.value("essay").orElse("");

        // 直接调用你的Java方法查库
        String weather = weatherTools.getWeather(city);
        PromptTemplate promptTemplate = new PromptTemplate(new ClassPathResource("prompts/weather-email.st"));
        // 2. 填充变量
        String prompt = promptTemplate.render(Map.of(
                "userName", userName,
                "city", city,
                "weather", weather,
                "essay",essay,
                "weather_info_formatted", "☀️ 天气：晴\n🌡️ 气温：18°C (体感 13°C)\n💧 湿度：15%\n💨 风速：10 km/h"
        ));
        // 这里可以让大模型帮你写一段穿衣建议
        String advice = chatClient.prompt(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,userId))
                .call().content();
        log.info("📍 [weatherNode] 执行完毕，天气: {}，建议: {}", weather, advice);
        return Map.of("weather_advice", advice);
    });

    AsyncNodeAction mailNode = AsyncNodeAction.node_async(state -> {
        String email = (String) state.value("email").orElse("");
        String advice = (String) state.value("weather_advice").orElse("");
        if(email == null || email.isEmpty()){
            log.error("❌ [mailNode] 发送失败：未找到收件人邮箱！");
            return Map.of("send_status", "发送失败：无邮箱");
        }
        // 直接调用你的Java方法查库
        String result = mailTool.sendEmail(email, "今日天气提醒",advice);
        log.info("📍 [mailNode] 执行完毕，结果: {}", result);
        return Map.of("send_status",result);
    });




    @Bean
    public CompiledGraph weatherPipelineGraph(WeatherTools weatherTools,MailTool mailTool,DataBaseQueryTool dataBaseQueryTool) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> {
            Map<String, KeyStrategy> map = new HashMap<>();
            map.put("userId", new ReplaceStrategy());
            map.put("address", new ReplaceStrategy());
            map.put("weather_advice", new ReplaceStrategy());
            map.put("email", new ReplaceStrategy());
            map.put("send_status", new ReplaceStrategy());
            map.put("essay",new ReplaceStrategy());
            return map;
        };


        StateGraph graph = new StateGraph(keyStrategyFactory)
                .addNode("queryAddress", queryAddressNode)
                .addNode("weather", weatherNode)
                .addNode("essay",hotNode)
                .addNode("sendEmail", mailNode)

                // 画线，固定死流程
                .addEdge(START, "queryAddress")
                .addEdge("queryAddress", "weather")
                .addEdge("weather", "essay")
                .addEdge("essay","sendEmail")
                .addEdge("sendEmail", END);

        return graph.compile();
    }

}
