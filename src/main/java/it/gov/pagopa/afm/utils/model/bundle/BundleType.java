package it.gov.pagopa.afm.utils.model.bundle;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import it.gov.pagopa.afm.utils.exception.AppException;
import lombok.Getter;

@Getter
public enum BundleType {
    GLOBAL("GLOBAL"),
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private final String value;

    BundleType(final String bundleType) {
        this.value = bundleType;
    }

    public static BundleType fromValue(String value) {
        return Arrays.stream(BundleType.values())
                .filter(elem -> elem.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "BundleType not found", "Cannot convert string '" + value + "' into enum"));
    }
}
