package it.gov.pagopa.afm.utils.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.gov.pagopa.afm.utils.model.ProblemJson;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.service.CDIService;

@RestController
public class ImportCDIController {
	
	@Autowired
	private CDIService cdiService;
	
	@Operation(summary = "Call to trigger the import of the CDIs and convert to bundles.", security = {@SecurityRequirement(name = "ApiKey")}, operationId = "syncCDI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtained bundle list.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(name = "BundleResponse", implementation = BundleResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/cdis/sync",
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<BundleResponse>> syncCDI(){
		return new ResponseEntity<>(cdiService.syncCDI(), HttpStatus.OK);
	}
}
