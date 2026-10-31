package com.glz.spring.helloworld.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

@RestController
public class MyEmController {

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @RequestMapping("addDoc")
    public String addDoc(){
        List<Document> docs = List.of(
                new Document("学java上java234.com"),
                new Document("java1234.com是个学java的好地方"),
                new Document("我喜欢游泳")
        );
        vectorStore.add(docs);
        return  "ok";
    }

    @RequestMapping("query")
    public String query(){
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder().query("去哪里学java").topK(2).build()
        );
        System.out.println(docs);
        return "OK";
    }

    @RequestMapping("/em.action")
    public String em(){
      float[] textVector =  embeddingModel.embed("衣服的质量杠杠的，很漂亮，不枉我等了这么久啊，喜欢，以后还来这里买");
        System.out.println(textVector.length + " " + Arrays.toString(textVector));
        return "OK";
    }
}
