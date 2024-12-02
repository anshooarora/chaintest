package com.aventstack.chaintest.api.build;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BuildSpec implements Specification<Build> {

    private final Build _build;

    public BuildSpec(final Build build) {
        _build = build;
    }

    @Override
    public Predicate toPredicate(final Root<Build> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        if (null != _build.getProjectId() && _build.getProjectId() > 0) {
            predicates.add(cb.equal(root.get(Build_.projectId), _build.getProjectId()));
        }

        final Predicate predicate = cb.and(predicates.toArray(new Predicate[0]));
        return predicate;
    }
}
