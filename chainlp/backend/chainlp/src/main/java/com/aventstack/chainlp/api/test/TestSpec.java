package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.tag.Tag;
import com.aventstack.chainlp.api.tag.Tag_;
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

    public TestSpec(final Test test, final String op) {
        _test = test;
        _op = StringUtils.isBlank(op) || op.equalsIgnoreCase("and")
                ? Predicate.BooleanOperator.AND : Predicate.BooleanOperator.OR;
    }

    @Override
    public Predicate toPredicate(final Root<Test> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        if (null != _test.getTags() && !_test.getTags().isEmpty()) {
            final Set<String> tagNames = _test.getTags().stream()
                    .map(Tag::getName).collect(Collectors.toSet());
            predicates.add(
                cb.and(
                    root.join(Test_.tags).get(Tag_.name).in(tagNames)
                )
            );
        }

        addPredicateIfNotZero(predicates, cb, root.get(Test_.id), _test.getId());
        addPredicateIfNotBlank(predicates, cb, root.get(Test_.name), _test.getName());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.projectId), _test.getProjectId());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.buildId), _test.getBuildId());
        addPredicateIfNotNull(predicates, cb, root.get(Test_.depth), _test.getDepth());
        addPredicateIfNotBlank(predicates, cb, root.get(Test_.error), _test.getError());
        addPredicateIfNotBlank(predicates, cb, root.get(Test_.result), _test.getResult());

        return _op.equals(Predicate.BooleanOperator.AND)
                ? cb.and(predicates.toArray(new Predicate[0])) : cb.or(predicates.toArray(new Predicate[0]));
    }

    private void addPredicateIfNotZero(final List<Predicate> predicates, final CriteriaBuilder cb, Path<?> path, final long value) {
        if (value != 0) {
            predicates.add(cb.equal(path, value));
        }
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

}
