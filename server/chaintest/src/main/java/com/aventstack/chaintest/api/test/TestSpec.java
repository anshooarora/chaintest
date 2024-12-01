package com.aventstack.chaintest.api.test;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TestSpec implements Specification<Test> {

    private final Test _test;
    private final Predicate.BooleanOperator _op;

    public TestSpec(final Test test) {
        _test = test;
        _op = Predicate.BooleanOperator.AND;
    }

    public TestSpec(final Test test, final Predicate.BooleanOperator op) {
        _test = test;
        _op = op;
    }

    @Override
    public Predicate toPredicate(final Root<Test> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isBlank(_test.getName())) {
            predicates.add(cb.like(cb.lower(root.get(Test_.name)), "%" + _test.getName().toLowerCase() + "%"));
        }

        if (_test.getProjectId() > 0) {
            predicates.add(cb.equal(root.get(Test_.projectId), _test.getProjectId()));
        }

        if (_test.getBuildId() > 0) {
            predicates.add(cb.equal(root.get(Test_.buildId), _test.getBuildId()));
        }

        if (null != _test.getDepth()) {
            predicates.add(cb.equal(root.get(Test_.depth), _test.getDepth()));
        }

        if (null != _test.getTags() && !_test.getTags().isEmpty()) {
            predicates.add(root.get(Test_.tags.getName()).in(_test.getTag()));
        }

        if (!StringUtils.isBlank(_test.getError())) {
            predicates.add(cb.like(cb.lower(root.get(Test_.error)), "%" + _test.getError().toLowerCase() + "%"));
        }

        if (!StringUtils.isBlank(_test.getResult())) {
            predicates.add(cb.equal(root.get(Test_.result), _test.getResult()));
        }

        Predicate predicate;
        if (_op.equals(Predicate.BooleanOperator.AND)) {
            predicate = cb.and(predicates.toArray(new Predicate[0]));
        } else {
            predicate = cb.or(predicates.toArray(new Predicate[0]));
        }

        return predicate;
    }

}
