package ru.practicum.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.stats", "ru.practicum.common"})
public class StatsServerApp {
    public static void main(String[] args) {
        SpringApplication.run(StatsServerApp.class, args);
    }
}
