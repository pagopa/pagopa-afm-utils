package it.gov.pagopa.afm.calculator.util;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@AllArgsConstructor
public class BundleSpecification implements Specification<Bundle> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Bundle> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Path<Object> key;
        if (criteria.getKey().contains(".")) {
            key = getNestedKey(root, criteria.getKey());
        } else {
            key = root.get(criteria.getKey());
        }
        Object value = criteria.getValue();
        if (value == null) {
            return null;
        }
        switch (criteria.getOperation()) {
            case EQUAL:
                return builder.equal(key, value);
            case EQUAL_OR_NULL:
                var spec1 = builder.equal(key, value);
                var spec2 = builder.isNull(key);
                return builder.or(spec1, spec2);
            case IN:
                return builder.in(key).value(value);
            case GREATER_THAN:
                return builder.greaterThan(key.as(int.class), (int) value);
            case LESS_THAN_EQUAL:
                return builder.lessThanOrEqualTo(key.as(int.class), (int) value);
            default:
                break;
        }
        return null;
    }

    private Path<Object> getNestedKey(From<?, ?> root, String key) {
        if (key.contains(".")) {
            int i = key.indexOf(".");
            var groupJoin = root.join(key.substring(0, i));
            return getNestedKey(groupJoin, key.substring(i + 1));
        } else {
            return root.get(key);
        }
    }

}

