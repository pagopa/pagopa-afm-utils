package it.gov.pagopa.afm.calculator.repository;

import it.gov.pagopa.afm.calculator.entity.CiBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiBundleRepository extends JpaRepository<CiBundle, String> {
}
