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

    public TestSpec(final Test test) {
        _test = test;
    }

    @Override
    public Predicate toPredicate(final Root<Test> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isBlank(_test.getName())) {
            predicates.add(cb.like(cb.lower(root.get(Test_.name)), "%" + _test.getName().toLowerCase() + "%"));
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

        final Predicate predicate = cb.and(predicates.toArray(new Predicate[0]));
        return predicate;
    }

}
