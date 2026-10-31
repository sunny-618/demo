package com.glz.spring.helloworld.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class StoryboardTool {

    @Tool(description = "根据脚本生成分镜描述和画面提示词")
    public String generateStoryboard(String script){
        return """
                分镜1:近景，人物出境，字幕突出痛点
                分镜2:产品/方案展示，画面明亮
                分镜3:对比效果，增强信任
                分镜4:行动号召，引导点击
                """;
    }
}
