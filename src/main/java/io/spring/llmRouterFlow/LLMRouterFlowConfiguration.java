/*
 * Copyright 2025 - 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.llmRouterFlow;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.QueueChannelSpec;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class LLMRouterFlowConfiguration {

    List<String> supportRoutes = List.of("billing", "technical", "account", "product");

    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel billingChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel accountChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel productChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel agentChannel() {
        return new DirectChannel();
    }

    @Bean
    public AbstractMessageRouter router(ChatClient.Builder chatClientBuilder) {
        LLMRouter router = new LLMRouter(chatClientBuilder, this.supportRoutes);
        router.setDefaultOutputChannelName("agentChannel");  // fallback
        return router;
    }

    @Bean
    public IntegrationFlow myFlow(AbstractMessageRouter router) {
        QueueChannelSpec spec = MessageChannels.queue();
        return IntegrationFlow.from("inputChannel")
                .route(router)
                .get();
    }

    @Bean
    public IntegrationFlow billingLogger(ChatClient.Builder chatClientBuilder) {
        return IntegrationFlow.from("billingChannel").handle(new IssueResponseHandler(chatClientBuilder, """
                You are a billing support specialist. Follow these guidelines:
                1. Always start with "Billing Support Response:"
                2. First acknowledge the specific billing issue
                3. Explain any charges or discrepancies clearly
                4. List concrete next steps with timeline
                5. End with payment options if relevant
                
                Keep responses professional but friendly.
                
                Input: """)).get();
    }

    @Bean
    public IntegrationFlow accountLogger(ChatClient.Builder chatClientBuilder) {
        return IntegrationFlow.from("accountChannel").handle(new IssueResponseHandler(chatClientBuilder, """
                    You are an account security specialist. Follow these guidelines:
                    1. Always start with "Account Support Response:"
                    2. Prioritize account security and verification
                    3. Provide clear steps for account recovery/changes
                    4. Include security tips and warnings
                    5. Set clear expectations for resolution time

                    Maintain a serious, security-focused tone.

                    Input: """)).get();
    }

    @Bean
    public IntegrationFlow productLogger(ChatClient.Builder chatClientBuilder) {
        return IntegrationFlow.from("productChannel").handle(new IssueResponseHandler(chatClientBuilder, """
                    You are a product specialist. Follow these guidelines:
                    1. Always start with "Product Support Response:"
                    2. Focus on feature education and best practices
                    3. Include specific examples of usage
                    4. Link to relevant documentation sections
                    5. Suggest related features that might help

                    Be educational and encouraging in tone.

                    Input: """)).get();
    }

    @Bean
    public IntegrationFlow agentLogger(ChatClient.Builder chatClientBuilder) {
        return IntegrationFlow.from("agentChannel").handle(new IssueResponseHandler(chatClientBuilder, """
                You are a technical support engineer. Follow these guidelines:
                1. Always start with "Technical Support Response:"
                2. List exact steps to resolve the issue
                3. Include system requirements if relevant
                4. Provide workarounds for common problems
                5. End with escalation path if needed
                
                Use clear, numbered steps and technical details.
                
                Input: """)).get();
    }
}
