package it.gov.pagopa.afm.utils.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.afm.utils.entity.Bundle;
import it.gov.pagopa.afm.utils.entity.CDI;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleRepository extends CosmosRepository<Bundle, String> {

  List<Bundle> findByIdCdiIsNotNull();
}
