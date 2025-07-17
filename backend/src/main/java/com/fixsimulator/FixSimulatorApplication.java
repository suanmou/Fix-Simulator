package com.fixsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import quickfix.MessageFactory;
import quickfix.MessageFactoryImpl;

@SpringBootApplication
public class FixSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixSimulatorApplication.class, args);
    }

    @Bean
    public MessageFactory messageFactory() {
        return new MessageFactoryImpl();
    }
}