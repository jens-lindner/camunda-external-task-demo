package com.example.demo;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HandlerConfiguration {

    @Bean
    @ExternalTaskSubscription("PizzaTopping")
    public ExternalTaskHandler pizzaToppingHandler() {
        return (externalTask, externalTaskService) -> {
            System.out.println(new Date());

            Map<String, Object> variables = new HashMap<>();
            long currentTimeMillis = System.currentTimeMillis();
            if( currentTimeMillis % 2 == 0) {
                variables.put("selectedTopping", "Thunfisch");
            } else {
                variables.put("selectedTopping", "4 Käse");
            }

            externalTaskService.complete(externalTask, variables);
        };
    }

}