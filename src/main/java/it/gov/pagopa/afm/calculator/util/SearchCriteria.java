package it.gov.pagopa.afm.calculator.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {

    /**
     * name of an attribute
     */
    private String key;

    /**
     * Search Operation, for example: EQUAL
     */
    private SearchOperation operation;

    /**
     * expected value
     */
    private Object value;
}
