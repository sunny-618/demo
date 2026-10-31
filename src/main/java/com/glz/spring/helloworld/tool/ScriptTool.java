package com.glz.spring.helloworld.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ScriptTool {

    @Tool(description = "根据主题生成短视频口播脚本")
    public String generateScript(String topic,String platform, String tone){
        return """
                脚本主题：%s
                语气：%s
                时长：30秒
                结构：钩子3秒 -> 痛点5秒 -> 方案15秒 -> 行动号召7秒
                """.formatted(topic,platform,tone);
    }
}
