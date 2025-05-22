package it.gov.pagopa.afm.utils.task;

import it.gov.pagopa.afm.utils.service.MarketPlaceClient;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class SchedulerTask {

  @Autowired private ThreadPoolTaskScheduler taskScheduler;

  @Autowired private MarketPlaceClient marketPlaceClient;

  @Autowired private CronTrigger cronTrigger;

  @PostConstruct
  public void scheduleRunnableWithCronTrigger() {
    MarketPlaceTrigger marketPlaceTrigger = new MarketPlaceTrigger(marketPlaceClient);

    taskScheduler.schedule(marketPlaceTrigger, cronTrigger);
  }
}
