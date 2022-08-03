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
        // get attribute reference
        Path<Object> key = getNestedKey(root, criteria.getKey());
        // get expected value
        Object value = criteria.getValue();

        // ignore null values
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
            var groupJoin = root.join(key.substring(0, i));
            return getNestedKey(groupJoin, key.substring(i + 1));
        } else {
            return root.get(key);
        }
    }

}

