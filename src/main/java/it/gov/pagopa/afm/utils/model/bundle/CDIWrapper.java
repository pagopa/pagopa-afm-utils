package it.gov.pagopa.afm.utils.model.bundle;

import it.gov.pagopa.afm.utils.entity.CDI;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CDIWrapper {
  private List<CDI> cdiItems;
}
