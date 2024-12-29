package com.aventstack.chainlp.api.appenv;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AppEnvSpec implements Specification<AppEnv> {

    private final AppEnv _appenv;

    public AppEnvSpec(final AppEnv appEnv) {
        _appenv = appEnv;
    }

    @Override
    public Predicate toPredicate(final Root<AppEnv> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        addPredicateIfNotZero(predicates, cb, root.get(AppEnv_.id), _appenv.getId());
        addPredicateIfNotNull(predicates, cb, root.get(AppEnv_.k), _appenv.getK());

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void addPredicateIfNotZero(final List<Predicate> predicates, final CriteriaBuilder cb, Path<?> path, final long value) {
        if (value != 0) {
            predicates.add(cb.equal(path, value));
        }
    }

    private void addPredicateIfNotNull(final List<Predicate> predicates, final CriteriaBuilder cb, Path<?> path, final Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

}
