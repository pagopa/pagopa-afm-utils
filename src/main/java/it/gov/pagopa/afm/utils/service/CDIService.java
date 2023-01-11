package it.gov.pagopa.afm.utils.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.gov.pagopa.afm.utils.entity.CDI;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CDIService {
	@Autowired
    private CDICollectionRepository cdisRepository;
	
	public CDI updateCDI(CDI cdiEntity) {
        return cdisRepository.save(cdiEntity);
    }
    
    public void deleteCDI(CDI cdiEntity) {
    	cdisRepository.delete(cdiEntity);	
    } 
}
