package it.gov.pagopa.afm.calculator.model.configuration;

import it.gov.pagopa.afm.calculator.entity.Bundle;
import it.gov.pagopa.afm.calculator.entity.CiBundle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    List<Bundle> bundles;
    List<CiBundle> ciBundles;
}
