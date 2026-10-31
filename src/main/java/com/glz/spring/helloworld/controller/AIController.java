package com.glz.spring.helloworld.controller;

import com.glz.spring.helloworld.service.AIService;
import com.glz.spring.helloworld.service.ToolMemoryAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ToolMemoryAdvisorService toolMemoryAdvisorService;

    @GetMapping("/demo")
    public String demo(@RequestParam String question){
        System.out.println("question:" +  question);
        String answer = aiService.chatDemo(question);
        System.out.println("answer: " + answer);
        return answer;
    }

    @GetMapping("/prompt")
    public String demo1(){
        aiService.promptDemo();
        return "ok";
    }

    @GetMapping("/stream")
    public String stream(){
        aiService.streamDemo();
        return "ok";
    }

    @GetMapping("/struct")
    public String struct(){
        aiService.structDemo();
        return "ok";
    }

    @GetMapping("/embedding")
    public String embedding(){
        aiService.embedding();
        return "ok";
    }
    @GetMapping("/rag")
    public String rag(){
        aiService.rag();
        return "ok";
    }

    @GetMapping("/advisor")
    public String advisor(){
        aiService.ragAdvisorDemo();
        return "ok";
    }

    @GetMapping("/multiStepAgentDemo")
    public String multiStepAgentDemo(){
        aiService.multiStepAgentDemo();
        return "ok";
    }

    @GetMapping("/toolCallingDemo")
    public String toolCallingDemo(){
        aiService.toolCallingDemo();
        return "ok";
    }

    @GetMapping("/chatMemoryDemo")
    public String chatMemoryDemo(){
        aiService.chatMemoryDemo();
        return "ok";
    }

    @GetMapping("/toolMemoryAdvisorDemo")
    public String toolMemoryAdvisorDemo(){
        toolMemoryAdvisorService.toolMemoryAdvisorDemo();
        return "ok";
    }


}



