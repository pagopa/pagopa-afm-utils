package it.gov.pagopa.afm.utils.model.bundle;

import java.util.List;

import it.gov.pagopa.afm.utils.entity.CDI;
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
public class Wrapper {
	private List<CDI> cdiItems;
}
