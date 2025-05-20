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

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
@EnableIntegration
public class LlmRouterFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmRouterFlowApplication.class, args);
    }

    /**
     * Creates an ApplicationRunner bean that simulates customer support tickets.
     * This runner sends sample customer tickets to the input channel when the application starts,
     * demonstrating the routing capabilities of the system with different types of support requests.
     *
     * @param inputChannel the message channel to send the sample tickets to
     * @return an ApplicationRunner that executes when the application starts
     */
    @Bean
    ApplicationRunner applicationRunner(MessageChannel inputChannel) {
        return args -> {
            List<String> tickets = List.of(
                    """
                            Subject: Can't access my account
                            Message: Hi, I've been trying to log in for the past hour but keep getting an 'invalid password' error.
                            I'm sure I'm using the right password. Can you help me regain access? This is urgent as I need to
                            submit a report by end of day.
                            - John""",

                    """
                            Subject: Unexpected charge on my card
                            Message: Hello, I just noticed a charge of .99 on my credit card from your company, but I thought
                            I was on the .99 plan. Can you explain this charge and adjust it if it's a mistake?
                            Thanks,
                            Sarah""",

                    """
                            Subject: How to export data?
                            Message: I need to export all my project data to Excel. I've looked through the docs but can't
                            figure out how to do a bulk export. Is this possible? If so, could you walk me through the steps?
                            Best regards,
                            Mike""");

            int i = 1;
            for (String ticket : tickets) {
                System.out.println("\nTicket " + i++);
                System.out.println("------------------------------------------------------------");
                System.out.println(ticket);
                System.out.println("------------------------------------------------------------");
                inputChannel.send(MessageBuilder.withPayload(ticket).build());
            }
        };
    }

}
