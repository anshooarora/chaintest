package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.tag.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestSpec implements Specification<Test> {

    private final Test _test;
    private final Predicate.BooleanOperator _op;

    public TestSpec(final Test test) {
        _test = test;
        _op = Predicate.BooleanOperator.AND;
    }

    public TestSpec(final Test test, final Predicate.BooleanOperator op) {
        _test = test;
        _op = op != null ? op : Predicate.BooleanOperator.AND;
    }

    @Override
    public Predicate toPredicate(final Root<Test> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        addPredicateIfNotBlank(predicates, cb, root.get(Test_.name), _test.getName());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.projectId), _test.getProjectId());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.buildId), _test.getBuildId());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.depth), _test.getDepth());
        addTagsPredicate(predicates, root, _test.getTags());
        addPredicateIfNotBlank(predicates, cb, root.get(Test_.error), _test.getError());
        addPredicateIfNotBlank(predicates, cb, root.get(Test_.result), _test.getResult());

        return _op.equals(Predicate.BooleanOperator.AND)
                ? cb.and(predicates.toArray(new Predicate[0])) : cb.or(predicates.toArray(new Predicate[0]));
    }

    private void addPredicateIfNotBlank(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (!StringUtils.isBlank(value)) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    private void addPredicateIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    private void addTagsPredicate(List<Predicate> predicates, Root<Test> root, Set<Tag> tags) {
        if (tags != null && !tags.isEmpty()) {
            Set<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toSet());
            predicates.add(root.get(Test_.tags.getName()).in(tagNames));
        }
    }

}