package it.gov.pagopa.afm.utils.config;

import com.azure.spring.data.cosmos.common.ExpressionResolver;
import it.gov.pagopa.afm.utils.repository.BundleRepository;
import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

  @Bean
  @Primary
  BundleRepository bundleRepository() {
    return Mockito.mock(BundleRepository.class);
  }
}
