package com.aventstack.chainlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ChainApp {

    public static void main(String[] args) {
        SpringApplication.run(ChainApp.class, args);
    }

}
