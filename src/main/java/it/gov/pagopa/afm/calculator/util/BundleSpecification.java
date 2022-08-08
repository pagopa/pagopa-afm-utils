package it.gov.pagopa.afm.calculator.util;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class BundleSpecification implements Specification<Bundle> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Bundle> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // get attribute reference
        Path<Object> key = getNestedKey(root, criteria.getKey());
        // get expected value
        Object value = criteria.getValue();

        // ignore null values
        if (Boolean.TRUE.equals(criteria.getIgnoreNull()) && value == null) {
            return null;
        }
        query.distinct(true);
        switch (criteria.getOperation()) {
            case EQUAL:
                return builder.equal(key, value);
            case NULL:
                return builder.isNull(key);
            case NULL_OR_EQUAL:
                var spec1 = builder.equal(key, value);
                var spec2 = builder.isNull(key);
                return builder.or(spec1, spec2);
            case IN:
                return builder.in(key).value(value);
            case NOT_IN:
                return builder.not(builder.in(key).value(value));
            case LESS_THAN:
                return builder.lessThan(key.as(int.class), (int) value);
            case LESS_THAN_EQUAL:
                return builder.lessThanOrEqualTo(key.as(int.class), (int) value);
            case GREATER_THAN:
                return builder.greaterThan(key.as(int.class), (int) value);
            case GREATER_THAN_EQUAL:
                return builder.greaterThanOrEqualTo(key.as(int.class), (int) value);
            default:
                break;
        }
        return null;
    }

    /**
     * This recursive function get the attribute reference, {@code key} is the name of the attribute of root.
     * <pre>
     * Example
     *
     * USER
     *  - email: String
     *  - role: Role
     *
     * ROLE:
     *  - name: String
     *
     * You can access to basic attribute with key = "email"
     * You can access to nested attribute with this syntax: "role.name"
     * </pre>
     *
     * @param root Entity
     * @param key  Attribute name
     * @return path corresponding to the referenced attribute
     */
    private Path<Object> getNestedKey(From<?, ?> root, String key) {
        if (key.contains(".")) {
            int i = key.indexOf(".");
            // we need always left join
            var groupJoin = root.join(key.substring(0, i), JoinType.LEFT);
            return getNestedKey(groupJoin, key.substring(i + 1));
        } else {
            return root.get(key);
        }
    }

}

