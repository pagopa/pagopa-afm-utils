package it.gov.pagopa.afm.utils.model.bundle;

import java.util.ArrayList;
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
public class BundleWrapper {
	private String idPsp;
	@Builder.Default
	private List<BundleRequest> bundleRequests = new ArrayList<>();
}
