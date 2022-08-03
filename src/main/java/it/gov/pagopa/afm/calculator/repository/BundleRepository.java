package it.gov.pagopa.afm.calculator.repository;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, String>, JpaSpecificationExecutor<Bundle> {
}
