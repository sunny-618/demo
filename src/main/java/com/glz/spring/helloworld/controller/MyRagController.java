package com.glz.spring.helloworld.controller;


import com.glz.spring.helloworld.util.DocumentParseUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyRagController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private DocumentParseUtil documentParseUtil;

    @RequestMapping("addRag")
    public String addDocs(){
        addChunkedDocuments("/Users/collion/Documents/HelloWorld/upload/java介绍.md","java介绍.md");
        addChunkedDocuments("/Users/collion/Documents/HelloWorld/upload/java简介.docx","java简介.docx");
        return "ok";
    }

    public void addChunkedDocuments(String filePath,String label){
        List<Document> raw = documentParseUtil.parse(filePath);
        List<Document> chunks = tokenTextSplitter.apply(raw);
        vectorStore.add(chunks);
        System.out.println(label + ",向量存储添加成功！（共 " + chunks.size() + " 块）");
    }

    @RequestMapping("query2")
    public String query2(){
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("介绍下java1234")
                        .topK(3)
                        .build()
        );
        System.out.println(docs);
        return "ok";
    }

    @RequestMapping("ask_rag")
    public String askRag(){
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(3) //搜索结果数量
                        .similarityThreshold(0.7) // 相似度阈值0-1值越大要求越严格，过滤低相关性的结果，提高RAG回答问题质量
                        .build())
                .build();
        String result = chatClient.prompt()
                .advisors(advisor)
                .advisors(spec -> spec.param(
                        ChatMemory.CONVERSATION_ID,             // 会话 ID 的键
                        "your-session-id"                       // 你的会话标识
                ))
                .user("介绍下java234")
                .call()
                .content();
        System.out.println(result);
        return "OK";
    }
}
