package it.gov.pagopa.afm.utils.repository;

import com.azure.cosmos.models.PartitionKey;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import it.gov.pagopa.afm.utils.entity.Bundle;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleRepository extends CosmosRepository<Bundle, String> {

  List<Bundle> findByIdCdiIsNotNull();

  List<Bundle> findByIdCdi(String idCdi, PartitionKey pspCode);
}
