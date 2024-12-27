package com.aventstack.chainlp.api.secret;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecretsService {

    @Autowired
    private SecretsRepository repository;

    public void create(final Secret secret) {
        log.debug("Creating secret");
        repository.save(secret);
        log.info("Created secret");
    }

    public void update(final Secret secret) {
        repository.save(secret);
        log.info("Updated secret");
    }

    public void delete(final int id) {
        log.debug("Deleting secret with id: [{}]", id);
        repository.deleteById(id);
        log.info("Deleted secret with id: [{}]", id);
    }

}
