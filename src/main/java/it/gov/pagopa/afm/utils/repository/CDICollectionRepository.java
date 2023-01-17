package it.gov.pagopa.afm.utils.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;

import it.gov.pagopa.afm.utils.entity.CDI;

@Repository
public interface CDICollectionRepository extends CosmosRepository<CDI, String> {
	@Query("select * from Items r where r.cdiStatus = 'NEW'")
    List<CDI> getWorkableCDIs();
}
