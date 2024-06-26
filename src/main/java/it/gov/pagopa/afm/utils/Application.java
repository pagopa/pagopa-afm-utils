package it.gov.pagopa.afm.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@DependsOn("expressionResolver")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
