package it.gov.pagopa.afm.calculator.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {

    /**
     * name of an attribute
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


    /**
     * ignore this search criteria if value is null
     */
    private Boolean ignoreNull = Boolean.TRUE;

    public SearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    /**
     * If ignoreNull is set to true then this criteria is ignored if the value is null
     *
     * @return this object with ignoreNull set
     */
    public SearchCriteria withIgnoreNull(Boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }
}
