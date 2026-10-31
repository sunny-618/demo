package com.glz.spring.helloworld.bean;


import java.util.List;

public record FeedbackAnalysis (
        String sentiment,
        List<String> keywords,
        String summary
){}
