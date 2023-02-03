package it.gov.pagopa.afm.utils.task;

import it.gov.pagopa.afm.utils.service.MarketPlaceClient;

public class MarketPlaceTrigger implements Runnable {

  private final MarketPlaceClient marketPlaceClient;

  public MarketPlaceTrigger(MarketPlaceClient marketPlaceClient) {
    this.marketPlaceClient = marketPlaceClient;
  }

  @Override
  public void run() {
    marketPlaceClient.getConfiguration();
  }
}
