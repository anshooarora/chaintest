package com.aventstack.chaintest.api.test.history;

import com.aventstack.chaintest.api.test.Test;
import com.aventstack.chaintest.api.test.Test_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TestHistorySpec implements Specification<Test> {

    private final Test _test;

    public TestHistorySpec(final Test test) {
        _test = test;
    }

    @Override
    public Predicate toPredicate(final Root<Test> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(Test_.projectId), _test.getProjectId()));
        predicates.add(cb.equal(root.get(Test_.className), _test.getClassName()));
        predicates.add(cb.equal(root.get(Test_.name), _test.getName()));
        predicates.add(cb.lessThan(root.get(Test_.id), _test.getId()));

        final Predicate predicate = cb.and(predicates.toArray(new Predicate[0]));
        return predicate;
    }

}
