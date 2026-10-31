package com.glz.spring.helloworld.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class WeatherTools {

    static final ObjectMapper MAPPER = new ObjectMapper();
    static final Map<String, String> WEATHER_MAP = Map.of(
            "Sunny", "晴", "Clear", "晴", "Partly Cloudy", "多云",
            "Overcast", "阴", "Smoky haze", "烟霾", "Mist", "薄雾",
            "Light rain shower", "小阵雨", "Heavy rain", "大雨",
            "Light drizzle", "毛毛雨"
    );

    @Tool(description = "查询某个城市当前的天气情况")
    public String getWeather(@ToolParam(description = "城市名称，例如 北京") String city) {
            try {
                String url = "http://wttr.in/" + city + "?format=j1&lang=zh";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("User-Agent", "curl/7.68.0")
                        .GET()
                        .build();
                HttpResponse<String> resp = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) throw new RuntimeException("HTTP " + resp.statusCode());

                JsonNode data = MAPPER.readTree(resp.body());
                JsonNode current = data.get("current_condition").get(0);
                JsonNode area = data.get("nearest_area").get(0);
                String rawDesc = current.get("weatherDesc").get(0).get("value").asText();
                String desc = WEATHER_MAP.getOrDefault(rawDesc, rawDesc);

                return String.format(
                        "【%s天气】%n天气：%s%n温度：%s°C（体感 %s°C）%n湿度：%s%%%n风速：%s km/h%n",
                        area.get("areaName").get(0).get("value").asText(),
                        desc,
                        current.get("temp_C").asText(),
                        current.get("FeelsLikeC").asText(),
                        current.get("humidity").asText(),
                        current.get("windspeedKmph").asText()
                );
            } catch (Exception e) {
                return String.format(
                        "【%s天气】(模拟数据，网络不通时显示)%n 天气：晴，温度：28C（体感30C），湿度：65%%， 风速：12km/h%n",
                        city
                );
            }
    }
}
