package it.gov.pagopa.afm.utils;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.model.bundle.BundleResponse;
import it.gov.pagopa.afm.utils.model.bundle.Wrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCDIHandler extends FunctionInvoker<Wrapper, List<BundleResponse>> {
		
	@FunctionName("importCDIFunction")
	public List<BundleResponse> execute(
			@CosmosDBTrigger(
					name = "CDIDatastoreTrigger",
					databaseName = "db",
					collectionName = "cdis",
					leaseCollectionName = "cdis-leases",
					createLeaseCollectionIfNotExists = true,
					connectionStringSetting = "COSMOS_CONN_STRING") 
			List<CDI> items,
			ExecutionContext context) {
		
		log.info("Import CDI function executed at: " + LocalDateTime.now() + " for CDI list with size: " + items.size());
		
		Wrapper wrapper = Wrapper.builder().cdiItems(items).build();
		return handleRequest(wrapper, context);
	}
}
