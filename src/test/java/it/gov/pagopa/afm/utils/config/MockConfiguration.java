package it.gov.pagopa.afm.utils.config;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.azure.spring.data.cosmos.common.ExpressionResolver;

import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;

@Configuration
public class MockConfiguration {
    @Bean
    @Primary
    ExpressionResolver expressionResolver() {
        return Mockito.mock(ExpressionResolver.class);
    }
    
    @Bean
    @Primary
    CDICollectionRepository cdisRepository() {
        return Mockito.mock(CDICollectionRepository.class);
    }
}
