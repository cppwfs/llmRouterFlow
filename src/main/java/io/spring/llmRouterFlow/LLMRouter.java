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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class LLMRouter extends AbstractMessageRouter {

    ChatClient chatClient;

    List<String> supportRoutes;

    public LLMRouter(ChatClient.Builder chatClientBuilder, List<String> supportRoutes) {
        this.chatClient = chatClientBuilder.build();
        this.supportRoutes = supportRoutes;}

    @Override
    protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
        String channelName = determineRoute((String)message.getPayload(),this.supportRoutes);
        return Collections.singletonList(getChannelResolver().resolveDestination(channelName+"Channel"));
    }


    /**
     * Analyzes the input content and determines the most appropriate route based on
     * content classification. The classification process considers key terms,
     * context,
     * and patterns in the input to select the optimal route.
     *
     * <p>
     * The method uses an LLM to:
     * <ul>
     * <li>Analyze the input content and context</li>
     * <li>Consider the available routing options</li>
     * <li>Provide reasoning for the routing decision</li>
     * <li>Select the most appropriate route</li>
     * </ul>
     *
     * @param input           The input text to analyze for routing
     * @param availableRoutes The set of available routing options
     * @return The selected route key based on content analysis
     */
    @SuppressWarnings("null")
    private String determineRoute(String input, Iterable<String> availableRoutes) {
        System.out.println("\nAvailable routes: " + availableRoutes);

        String selectorPrompt = String.format("""
                    Analyze the input and select the most appropriate support team from these options: %s
                    First explain your reasoning, then provide your selection in this JSON format:
                    
                    \\{
                        "reasoning": "Brief explanation of why this ticket should be routed to a specific team.
                                    Consider key terms, user intent, and urgency level.",
                        "selection": "The chosen team name"
                    \\}
                    
                    Input: %s""", availableRoutes, input);

        RoutingResponse routingResponse = chatClient.prompt(selectorPrompt).call().entity(RoutingResponse.class);

        System.out.printf("Routing Analysis:%s\nSelected route: %s%n",
                routingResponse.reasoning(), routingResponse.selection());

        return routingResponse.selection();
    }
}
