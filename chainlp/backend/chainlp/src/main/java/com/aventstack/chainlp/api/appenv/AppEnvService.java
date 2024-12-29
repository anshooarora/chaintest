package com.aventstack.chainlp.api.appenv;

import com.aventstack.chainlp.api.domain.NotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppEnvService {

    @Autowired
    private AppEnvRepository repository;

    public List<AppEnv> findAll(final int id, final String name) {
        System.getProperties().forEach((k, v) -> System.out.println(k + ":" + v));
        final AppEnv env = AppEnv.builder().id(id).k(name).build();
        final AppEnvSpec spec = new AppEnvSpec(env);
        return repository.findAll(spec);
    }

    public AppEnv findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AppEnv not found"));
    }

    public AppEnv create(@Valid AppEnv appEnv) {
        if (repository.findByK(appEnv.getK()).isPresent()) {
            throw new IllegalArgumentException("AppEnv with name " + appEnv.getK() + " already exists");
        }

        final AppEnv env = repository.save(appEnv);
        System.setProperty(appEnv.getK(), appEnv.getV());
        return env;
    }

    public void update(@Valid AppEnv appEnv) {
        repository.findById(appEnv.getId())
            .ifPresentOrElse(e -> {
                repository.save(appEnv);
                System.setProperty(appEnv.getK(), appEnv.getV());
                }, () -> { throw new NotFoundException("AppEnv not found"); });
    }

    public void delete(int id) {
        log.debug("Deleting AppEnv with id: {}", id);
        repository.findById(id)
            .ifPresentOrElse(env -> {
                repository.deleteById(id);
                System.clearProperty(env.getK());
                log.info("Deleted AppEnv with id: {}", id);
            }, () -> log.error("AppEnv with id: {} not found", id));
    }
}
