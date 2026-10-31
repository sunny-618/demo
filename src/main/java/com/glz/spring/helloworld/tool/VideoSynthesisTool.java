package com.glz.spring.helloworld.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VideoSynthesisTool {

    @Tool(description = "提交视频合成任务，返回任务ID")
    public String submitVideoTask(String script,String storyboard, String voice){
        String taskId  = "VT-" + UUID.randomUUID();
        return "视频任务已提交，taskId=" + taskId;
    }
}
