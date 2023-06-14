package it.gov.pagopa.afm.utils.repository;

import com.azure.cosmos.models.PartitionKey;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import it.gov.pagopa.afm.utils.entity.Bundle;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository extends CosmosRepository<Bundle, String> {

  List<Bundle> findByIdCdiIsNotNull();

  List<Bundle> findByIdCdi(String idCdi, PartitionKey pspCode);
}
