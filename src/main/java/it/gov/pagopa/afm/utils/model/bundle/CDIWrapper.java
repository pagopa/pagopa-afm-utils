package it.gov.pagopa.afm.utils.model.bundle;

import it.gov.pagopa.afm.utils.entity.CDI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CDIWrapper {
  private List<CDI> cdiItems;
}
