package com.glz.spring.helloworld.service;

import com.glz.spring.helloworld.tool.DateTimeTools;
import com.glz.spring.helloworld.tool.WeatherTools;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.advisor.api.Advisor;


import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private EmbeddingModel embeddingModel;


    public String chatDemo(String question){
       return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    public void promptDemo(){
        String topic = "递归";
        String audience = "10岁小学生";

        String answer = chatClient.prompt()
                .system("你是一名擅长打比方的科普老师。")
                .user(u->u.text("请用合适{audience}理解的方式，解释什么是{topic},不超过50个字")
                        .param("audience",audience)
                        .param("topic",topic))
                .call()
                .content();

           System.out.println("主题：" + topic + "(讲给：" + audience + ")");
           System.out.println("答：" + answer);
    }

    public void streamDemo(){
        String question = "写一首关于编程的四行小诗";
        System.out.println("问：" + question);
        System.out.println("答：");
        chatClient.prompt()
                .user(question)
                .stream()
                .content()
                .toStream()
                .forEach(System.out::print);
        System.out.println();
    }

    private record BooK(
            String title,
            String author,
            int year
    ){}

    public void  structDemo(){
        List<BooK> books = chatClient.prompt()
                .user("推荐3本经典的计算机科学书籍。")
                .call()
                .entity(new ParameterizedTypeReference<List<BooK>>() {
                });
        System.out.println("模型返回的结构化数据：");
        for (BooK b: books){
            System.out.printf("  - 《%s》 %s（%d）%n", b.title(), b.author(), b.year());
        }
    }

    public void embedding(){
        String a = "我喜欢喝咖啡";
        String b = "我爱喝拿铁";
        String c = "今天的股市大涨";

        float[] va = embeddingModel.embed(a);
        float[] vb = embeddingModel.embed(b);
        float[] vc = embeddingModel.embed(c);

        System.out.println("每个向量的维度：" + va.length);
        System.out.printf("相似度  「%s」 vs 「%s」 = %.4f（应较高）%n", a, b, cosine(va, vb));
        System.out.printf("相似度  「%s」 vs 「%s」 = %.4f（应较低）%n", a, c, cosine(va, vc));
    }

    private static double cosine(float[] x, float[] y) {
        double dot = 0, nx = 0, ny = 0;
        for (int i = 0; i < x.length; i++) {
            dot += x[i] * y[i];
            nx += x[i] * x[i];
            ny += y[i] * y[i];
        }
        return dot / (Math.sqrt(nx) * Math.sqrt(ny));
    }

    public void rag(){
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(List.of(
                new Document("公司内部Wiki的访问地址：https://baidu.com,需要工牌账号登陆"),
                new Document("报销流程：在 OA 系统提交申请，财务每周三统一打款。"),
                new Document("我们的旗舰产品代号为「天枢」，将于 2026 年第三季度发布。")
        ));
        String question = "我们的旗舰产品代号叫什么？什么时候发布？";
        List<Document> related = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(2).build());
        String context = related.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        System.out.println("【检索到的上下文】\n" + context + "\n");
        String answer = chatClient.prompt()
                .system("请只根据给定的「已知信息」回答问题：如果信息不足，就说不知道。")
                .user(u->u.text("""
                         已知信息：
                        {context}
                        
                        问题：{question}
                        """).param("context",context).param("question",question))
                .call()
                .content();
        System.out.println("问：" + question);
        System.out.println("答：" + answer);
    }


    public void ragAdvisorDemo(){
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(List.of(
                new Document("公司内部 Wiki 的访问地址是 https://wiki.acme.internal ，需用工牌账号登录。"),
                new Document("报销流程：在 OA 系统提交申请，财务每周三统一打款。"),
                new Document("我们的旗舰产品代号为「天枢」，将于 2026 年第三季度发布。")
        ));

        DocumentRetriever retriever = new LoggingRetriever(
                VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(2)
                        .build()
        );

        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();
        String question= "我们的旗舰产品代号叫什么？什么时候发布？";
        System.out.println("问：" + question + "\n");

        String answer = chatClient.prompt()
                .advisors(ragAdvisor)
                .user(question)
                .call()
                .content();
        System.out.println("\n答：" + answer);
        System.out.println("""

                对比示例 6：那边要自己 similaritySearch、自己拼上下文进提示词；
                这里只用 .advisors(ragAdvisor) 一行，检索与增强由 RetrievalAugmentationAdvisor 自动完成。
                它还是“模块化”的：查询改写、查询扩展、结果合并、提示增强都能各自替换，适合搭建更复杂的 RAG。
                """);
    }


    private record LoggingRetriever(DocumentRetriever delegate) implements DocumentRetriever{
        @Override
        public List<Document> retrieve(Query query) {
            List<Document> docs = delegate.retrieve(query);
            System.out.println("  🔎 RAG 自动检索：query=\"" + query.text() + "\" → 取回 " + docs.size() + " 篇：");
            docs.forEach(d -> System.out.println("     - " + d.getText()));
            return  docs;
        }
    }


    public void multiStepAgentDemo(){
        String task = "今天距离2026年12月31日还有多少天？请说明今天的日期";

        System.out.println("任务：" + task);

        String answer = chatClient.prompt()
                .user(task)
                .tools(new DateTimeTools())  // 提供时间相关工具，模型会按需多次调用
                .call()
                .content();
        System.out.println("结果：" + answer);
    }

    public void toolCallingDemo(){
        String question = "现在几点了？另外北京今天天气怎么样？";
        System.out.println("问 ：" + question);
        String answer = chatClient.prompt()
                .user(question)
                .tools(new DateTimeTools(),new WeatherTools())
                .call()
                .content();
        System.out.println("答 ： " + answer);
    }

    public void chatMemoryDemo(){
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        String conversationId = "user-1";
        String q1 = "我最喜欢的编程语言是Java，请记住";
        String a1 = ask(memoryAdvisor,conversationId,q1);
        System.out.println("第一轮 问：" + q1);
        System.out.println("第一轮回答：" + a1 + "\n");

        String q2 = "我刚才说我最喜欢的编程语言是什么？";
        String a2 = ask(memoryAdvisor,conversationId,q1);
        System.out.println("第二轮 问：" + q2);
        System.out.println("第二轮回答：" + a2 + "\n");

        System.out.println("\n（若第2轮答出「Java」，说明记忆生效了）");

    }

    public String ask(MessageChatMemoryAdvisor advisor, String conversationId, String question){
        return  chatClient.prompt()
                .advisors(advisor)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .user(question)
                .call()
                .content();
    }




}
