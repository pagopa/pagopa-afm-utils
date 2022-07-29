package it.gov.pagopa.afm.calculator.repository;

import it.gov.pagopa.afm.calculator.entity.CiBundleAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiBundleAttributeRepository extends JpaRepository<CiBundleAttribute, String> {
}
