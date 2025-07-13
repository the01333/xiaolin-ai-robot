package com.puxinxiaolin.ai.robot.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 记忆功能配置
 * @Author: YCcLin
 * @Date: 2025/7/13 13:51
 */
@Configuration
public class ChatMemoryConfig {

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)  // 记忆存储
                .maxMessages(50)  // 最大消息窗口
                .build();
    }

}
