package com.uday.mediahandlerservice.configurations.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan( basePackages = {"com.uday"})
@EnableAutoConfiguration
@Configuration
public class MediaHandlerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaHandlerServiceApplication.class, args);
    }

}
