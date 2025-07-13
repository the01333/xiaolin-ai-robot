package com.puxinxiaolin.ai.robot.config;

import com.puxinxiaolin.ai.robot.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: ChatClient 和 ChatModel 相比属于更上层的封装, 要使用我们需要进行配置
 * @Author: YCcLin
 * @Date: 2025/7/13 11:30
 */
@Configuration
public class ChatClientConfig {

    @Resource
    private ChatMemory chatMemory;

    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("请你扮演一名资历颇深的Java后端程序员, 现在在负责仿小红书的小林书项目开发")
                .defaultAdvisors(new SimpleLoggerAdvisor(),  // 添加 Spring AI 内置的日志记录功能
                        new MyLoggerAdvisor(),  // 自定义日志记录
                        MessageChatMemoryAdvisor.builder(chatMemory).build()   // 记忆化功能
                ).build();
    }

}
