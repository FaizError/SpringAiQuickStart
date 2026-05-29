package com.hzx.springaiquickstart.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Qualifier("deepseek")
    public ChatClient.Builder deepseekChatClientBuilder(DeepSeekChatModel deepSeekChatModel) {
        return ChatClient.builder(deepSeekChatModel);
    }

    @Bean
    @Qualifier("zhipuai")
    public ChatClient.Builder zhipuaiChatClientBuilder(ZhiPuAiChatModel zhiPuAiChatModel) {
        return ChatClient.builder(zhiPuAiChatModel);
    }

    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(
            @Qualifier("deepseek") ChatClient.Builder deepseekBuilder,
            @Qualifier("zhipuai") ChatClient.Builder zhipuaiBuilder,
            @Value("${app.ai.default-chat-model:deepseek}") String defaultModel) {

        return "zhipuai".equalsIgnoreCase(defaultModel) ? zhipuaiBuilder : deepseekBuilder;
    }
}
