package com.aventstack.chainlp.api.test.history;

import com.aventstack.chainlp.api.test.Test;
import com.aventstack.chainlp.api.test.Test_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

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
        predicates.add(cb.equal(root.get(Test_.name), _test.getName()));
        predicates.add(cb.lessThan(root.get(Test_.id), _test.getId()));

        if (null != _test.getClassName()) {
            predicates.add(cb.equal(root.get(Test_.className), _test.getClassName()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

}
