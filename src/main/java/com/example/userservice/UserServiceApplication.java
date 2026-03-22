package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class UserServiceApplication {
    private static final Logger logger = LogManager.getLogger(UserServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Запуск Spring Boot приложения User Service...");
        SpringApplication.run(UserServiceApplication.class, args);
        logger.info("Приложение User Service успешно запущено");
    }
}