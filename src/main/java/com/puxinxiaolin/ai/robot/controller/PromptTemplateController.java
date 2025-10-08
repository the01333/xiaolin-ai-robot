package com.puxinxiaolin.ai.robot.controller;

import com.puxinxiaolin.ai.robot.model.AIResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @Description: 提示词模板
 * @Author: YCcLin
 * @Date: 2025/10/3 15:49
 */
@RestController
@RequestMapping("/v7/ai")
public class PromptTemplateController {

    @Resource
    private OpenAiChatModel chatModel;

    // 抽取提示词模板到 .st 文件中
    @Value("$classpath:/prompts/code-assistant.st")
    private org.springframework.core.io.Resource template;

    /**
     * 自定义提示词模板占位符
     *
     * @param message
     * @param lang
     * @return
     */
    @GetMapping(value = "/generateStream3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream3(@RequestParam("message") String message,
                                            @RequestParam("lang") String lang) {
        // 系统角色提示词模板
        String systemPrompt = """
                你是一位资深 {lang} 开发工程师, 已经从业数十年，经验非常丰富。
                """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("lang", lang));

        // 用户角色提示词模板
        String userPrompt = """
                请严格遵循以下要求编写代码：
                1. 功能描述：{description}
                2. 代码需包含详细注释
                3. 使用业界最佳实践
                """;
        PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
        // 填充提示词占位符，转换为 Message 对象
        Message userMessage = promptTemplate.createMessage(Map.of("description", message));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder()
                            .v(text).build();
                });
    }

    /**
     * 自定义提示词模板占位符
     *
     * @param message
     * @param lang
     * @return
     */
    @GetMapping(value = "/generateStream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream2(@RequestParam("message") String message,
                                            @RequestParam("lang") String lang) {
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                        你是一位资深 <lang> 开发工程师。请严格遵循以下要求编写代码：
                                                1. 功能描述：<description>
                                                2. 代码需包含详细注释
                                                3. 使用业界最佳实践
                        """).build();
        // 填充提示词占位符，转换为 Prompt 提示词对象
        Prompt prompt = promptTemplate.create(Map.of("description", message, "lang", lang));

        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder()
                            .v(text).build();
                });
    }

    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> generateStream(@RequestParam("message") String message,
                                           @RequestParam("lang") String lang) {
        PromptTemplate promptTemplate = new PromptTemplate(template);
        // 填充提示词占位符，转换为 Prompt 提示词对象
        Prompt prompt = promptTemplate.create(Map.of("description", message, "lang", lang));

        return chatModel.stream(prompt)
                .mapNotNull(chatResponse -> {
                    Generation generation = chatResponse.getResult();
                    String text = generation.getOutput().getText();
                    return AIResponse.builder()
                            .v(text).build();
                });
    }

}
