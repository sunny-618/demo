//package com.glz.spring.helloworld.tool;
//
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.stereotype.Component;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class HotSpotTool {
//
//    @Tool(description = "获取微博、抖音、快手、小红书等平台的热点榜单，返回各平台前10条热点标题")
//
//
//    public List<String> getHotNews() {
//        String url = "https://60s.viki.moe/v2/douyin";
//        List<String> titles = new ArrayList<>();
//
//        try {
//            HttpClient client = HttpClient.newBuilder()
//                    .connectTimeout(Duration.ofSeconds(5))
//                    .build();
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .timeout(Duration.ofSeconds(10))
//                    .header("User-Agent", "Mozilla/5.0")
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            if (response.statusCode() != 200) {
//                return titles; // 请求失败返回空列表，不中断邮件流程
//            }
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(response.body());
//
//            // 先判断业务 code
//            if (root.path("code").asInt(-1) != 200) {
//                return titles;
//            }
//
//            JsonNode data = root.path("data");
//            if (data.isArray()) {
//                for (JsonNode item : data) {
//                    String title = item.path("title").asText("");
//                    if (!title.isEmpty()) {
//                        titles.add(title);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            // 网络异常、解析异常都返回空列表，保证邮件流程不因热点获取失败而中断
//            // 建议这里打一行日志：log.warn("获取抖音热点失败", e);
//        }
//        return titles;
//    }
//}
