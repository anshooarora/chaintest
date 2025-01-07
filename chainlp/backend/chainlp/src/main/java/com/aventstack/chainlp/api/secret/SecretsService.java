package com.aventstack.chainlp.api.secret;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class SecretsService {

    @Autowired
    private SecretsRepository repository;

    public void initialize() {
        log.info("Initializing all secrets");
        repository.findAll()
                .forEach(secret -> {
                    decode(secret);
                    log.debug("Setting system property: [{}]", secret.getK());
                    System.setProperty(secret.getK(), secret.getDecoded());
                });
        log.info("Initialized secrets");
    }

    public Optional<Secret> findByName(final String name) {
        return repository.findByK(name);
    }

    public void create(final Secret secret) {
        log.debug("Creating secret");
        update(secret);
        log.info("Created secret");
    }

    public void update(final Secret secret) {
        repository.findByK(secret.getK()).ifPresentOrElse(
            existing -> {
                secret.setId(existing.getId());
                log.debug("Updating existing secret");
            },
            () -> log.debug("Creating new secret")
        );
        System.setProperty(secret.getK(), secret.getV());
        repository.save(secret);
    }

    public void delete(final int id) {
        log.debug("Deleting secret with id: [{}]", id);
        repository.deleteById(id);
        log.info("Deleted secret with id: [{}]", id);
    }

    public void decode(final Secret secret) {
        secret.setDecoded(new String(Base64.getDecoder().decode(secret.getV())));
    }

}
