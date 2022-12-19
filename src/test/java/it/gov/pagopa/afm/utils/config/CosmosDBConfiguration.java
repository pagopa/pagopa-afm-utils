package it.gov.pagopa.afm.utils.config;


import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.spring.data.cosmos.common.ExpressionResolver;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.core.mapping.EnableCosmosAuditing;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;

import it.gov.pagopa.afm.utils.repository.CDICollectionRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
public class CosmosDBConfiguration {
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
