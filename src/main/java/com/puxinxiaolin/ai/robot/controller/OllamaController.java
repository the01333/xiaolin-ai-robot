package com.puxinxiaolin.ai.robot.controller;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v3/ai")
public class OllamaController {

    @Resource
    private OllamaChatModel chatModel;

    /**
     * 流式对话
     *
     * @param message
     * @return
     */
    @GetMapping("/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        Prompt prompt = new Prompt(message);

        return chatModel.stream(prompt)
                .mapNotNull(resp -> {
                    String text = resp.getResult().getOutput().getText();

                    return StringUtils.isNotBlank(text) ? text.replace("\n", "<br>") : text;
                });
    }

    /**
     * 普通对话
     *
     * @param message
     * @return
     */
    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        ChatResponse chatResponse = chatModel.call(new Prompt(message));

        return chatResponse.getResult().getOutput().getText();
    }

}
