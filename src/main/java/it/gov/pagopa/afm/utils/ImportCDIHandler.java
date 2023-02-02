package it.gov.pagopa.afm.utils;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.CDIWrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

@Slf4j
public class ImportCDIHandler extends FunctionInvoker<CDIWrapper, List<BundleResponse>> {

  @FunctionName("importCDIFunction")
  public /*@CosmosDBTrigger*/ /*List<BundleResponse>*/
  /*@HttpTrigger*/ HttpResponseMessage execute(

      /*
      @CosmosDBTrigger(
      		name = "CDIDatastoreTrigger",
      		databaseName = "db",
      		//		collectionName = "cdis",
      		//		leaseCollectionName = "cdis-leases",
      		//		createLeaseCollectionIfNotExists = true,
      		//		connectionStringSetting = "COSMOS_CONN_STRING"
      		containerName = "cdis",
      		leaseContainerName = "cdis-leases",
      		createLeaseContainerIfNotExists = true,
      		connection = "COSMOS_CONN_STRING"
      		) List<CDI> items,
      */
      // remove @HttpTrigger and @CosmosDBInput when @CosmosDBTrigger works for java function
      // Extension 4.x+
      // (see:
      // https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-cosmosdb-v2-trigger?tabs=in-process%2Cextensionv4&pivots=programming-language-java)
      @HttpTrigger(
              name = "CDIHttpTrigger",
              methods = {HttpMethod.GET},
              route = "cdis/sync",
              authLevel = AuthorizationLevel.ANONYMOUS)
          HttpRequestMessage<Optional<String>> request,
      @CosmosDBInput(
              name = "CDIDatastoreInput",
              databaseName = "db",
              containerName = "cdis",
              connection = "COSMOS_CONN_STRING",
              sqlQuery = "%TRIGGER_SQL_QUERY%")
          List<CDI> items,
      ExecutionContext context) {

    log.info(
        "Import CDI function executed at: "
            + LocalDateTime.now()
            + " for CDI list with size: "
            + items.size());

    CDIWrapper wrapper = CDIWrapper.builder().cdiItems(items).build();
    // @CosmosDBTrigger return
    // return handleRequest(wrapper, context);

    // @HttpTrigger return
    return request
        .createResponseBuilder(HttpStatus.OK)
        .body(handleRequest(wrapper, context))
        .header("Content-Type", "application/json")
        .build();
  }
}
