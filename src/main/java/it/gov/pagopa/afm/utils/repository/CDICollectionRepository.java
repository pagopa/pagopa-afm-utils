package it.gov.pagopa.afm.utils.repository;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;

import it.gov.pagopa.afm.utils.entity.CDI;

@Repository
public interface CDICollectionRepository extends CosmosRepository<CDI, String> {

}
