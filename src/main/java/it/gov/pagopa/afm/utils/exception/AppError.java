package it.gov.pagopa.afm.utils.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppError {
  BUNDLE_REQUEST_DATA_ERROR(HttpStatus.BAD_REQUEST, "Error in the bundle request data", "%s"),
  BUNDLE_NOT_FOUND_ERROR(
      HttpStatus.NOT_FOUND, "Not found error in the bundle configuration data", "%s"),
  BUNDLE_CONFLICT_ERROR(
      HttpStatus.CONFLICT, "A bundle with the same configuration already exists", "%s"),
  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),
  UNKNOWN(null, "Unexpected Exception", "Something was wrong");

  public final HttpStatus httpStatus;
  public final String title;
  public final String details;

  AppError(HttpStatus httpStatus, String title, String details) {
    this.httpStatus = httpStatus;
    this.title = title;
    this.details = details;
  }
}
