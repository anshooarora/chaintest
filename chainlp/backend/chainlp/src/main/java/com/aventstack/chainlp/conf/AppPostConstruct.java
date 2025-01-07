package com.aventstack.chainlp.conf;

import com.aventstack.chainlp.api.secret.SecretsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppPostConstruct {

    @Autowired
    private SecretsService secretsService;

    @PostConstruct
    public void init() {
        secretsService.initialize();
    }

}
