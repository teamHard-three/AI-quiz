package com.team.aiquiz;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import okhttp3.*;

import java.io.IOException;

@SpringBootTest
class AiQuizApplicationTests {

    @Test
    void contextLoads() throws IOException {
        String url = "https://openrouter.ai/api/v1/chat/completions";
        String token = "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"; // 替换为你的真实 Token

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
                .header("Authorization", "Bearer " + token)
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





