package com.aiquiz.aiquizs;

import com.aiquiz.aiquizs.config.AIConfigLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
@SpringBootTest
class AIquizApplicationTests {

    @Test
    void contextLoads() {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        String apiKey = AIConfigLoader.getApiKey();
        // 构造请求体
        JSONArray messages = new JSONArray()
                .put(new JSONObject()
                        .set("role", "user")
                        .set("content", "你是谁"));

        JSONObject requestBody = new JSONObject()
                .set("model", "deepseek/deepseek-chat-v3-0324:free")
                .set("messages", messages)
                .set("temperature", 0.7);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();

        String responseBody = response.body();


        // 解析返回的 JSON
        JSONObject json = new JSONObject(responseBody);
        String answer = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getStr("content");

        System.out.println("AI 回答: " + answer.trim());
    }

}
