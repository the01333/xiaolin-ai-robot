package com.puxinxiaolin.ai.robot.controller;

import com.puxinxiaolin.ai.robot.model.AIResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/v6/ai")
public class AliyunBailianController {

    @Resource
    private OpenAiChatModel chatModel;

    // 存储聊天对话
    private Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();

    /**
     * 流式对话, 数据格式为 text/event-stream 基于 SSE 实现服务器主动、单向向客户端推送数据（相当于流式输出）
     *
     * @param message
     * @return
     */
    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        SystemMessage systemMessage = new SystemMessage("请扮演一名资历颇深的Java后端程序员, 现在在负责仿小红书的小林书项目开发");
        UserMessage userMessage = new UserMessage(message);
        // 构建提示词
        Prompt prompt = new Prompt(Arrays.asList(systemMessage, userMessage));

        // 流式输出
        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder()
                            .v(text).build();
                });

    }

    /**
     * 普通对话
     *
     * @param message
     * @return
     */
    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message,
                           @RequestParam("chatId") String chatId) {
        List<Message> messages = chatMemoryStore.get(chatId);
        if (CollectionUtils.isEmpty(messages)) {
            messages = new ArrayList<>();
            chatMemoryStore.put(chatId, messages);
        }
        
        // 添加“用户角色消息”到聊天记录中
        messages.add(new UserMessage(message));
        Prompt prompt = new Prompt(messages);
        String responseText = chatModel.call(prompt).getResult().getOutput().getText();
        
        // 添加“助手角色消息”到聊天记录中
        messages.add(new AssistantMessage(responseText));
        return responseText;
    }

}
