package it.gov.pagopa.afm.calculator.util;

public enum SearchOperation {
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_EQUAL,
    LESS_THAN_EQUAL,
    NOT_EQUAL,
    EQUAL,
    NULL,
    NULL_OR_EQUAL, // if value is null or equal to expected
    LIKE,
    LIKE_START,
    LIKE_END,
    IN,
    NOT_IN
}
