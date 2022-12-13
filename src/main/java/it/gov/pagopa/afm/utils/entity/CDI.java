package it.gov.pagopa.afm.utils.entity;

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
public class CDI {
	private String idPsp;
	private String idCdi;
	private Boolean digitalStamp;
	private String validityDateFrom;
	private List<Detail> details;
}
