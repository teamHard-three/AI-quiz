package com.aiquiz.aiquizs;

import com.aiquiz.aiquizs.config.AIConfigLoader;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import okhttp3.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootTest
class AIquizApplicationTests {

    @Test
    void contextLoads() throws JSONException {


        String url = "https://openrouter.ai/api/v1/chat/completions";
        String apiKey = AIConfigLoader.getApiKey();
        String question="你是谁";
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", question));

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // 不限时
                .build();

        int maxRounds = 5;
        for (int round = 0; round < maxRounds; round++) {
            JSONObject body = new JSONObject()
                    .put("model", "deepseek/deepseek-chat-v3-0324:free")
                    .put("messages", messages)
                    .put("stream", true)
                    .put("temperature", 0.7);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                    .build();

            System.out.println("第 " + (round + 1) + " 轮 AI 流式回答：");

            StringBuilder fullAnswer = new StringBuilder();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("请求失败: " + response);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring("data: ".length()).trim();
                        if (data.equals("[DONE]")) break;

                        JSONObject deltaObj = new JSONObject(data);
                        JSONObject delta = deltaObj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("delta");

                        if (delta.has("content")) {
                            String chunk = delta.getString("content");
                            System.out.print(chunk);
                            fullAnswer.append(chunk);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(); // 换行

            String answer = fullAnswer.toString().trim();

            // 添加 AI 回复
            messages.put(new JSONObject()
                    .put("role", "assistant")
                    .put("content", answer));

            // 构造下一轮问题
            String nextQuestion = processAnswerToNextQuestion(answer);

            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", nextQuestion));
        }
    }

    private static String processAnswerToNextQuestion(String answer) {
        return "我最初的问题是什么";
    }
}


