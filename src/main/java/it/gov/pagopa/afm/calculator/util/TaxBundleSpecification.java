package it.gov.pagopa.afm.calculator.util;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * This Specification is too custom to use {@link BundleSpecification}
 *
 * The logic of the filter is:
 * or no transferCategoryList is specified for the bundle
 * or exists at least one transferCategory specified in the {@code value} field
 */
@AllArgsConstructor
public class TaxBundleSpecification implements Specification<Bundle> {

    private List<String> value;

    @Override
    public Predicate toPredicate(Root<Bundle> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // ignore null values
        if (value == null) {
            return null;
        }
        query.distinct(true);
        Join<Object, Object> join = root.join("transferCategoryList", JoinType.LEFT);
        var spec1 = builder.in(join.get("name")).value(value);
        // or no transferCategoryList is specified or transferCategory is contained in a specific list
        return builder.or(join.isNull(), spec1);
    }

}

