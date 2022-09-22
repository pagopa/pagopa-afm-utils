package it.gov.pagopa.afm.calculator.task;

import it.gov.pagopa.afm.calculator.service.MarketPlaceClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarketPlaceTrigger implements Runnable {

    private MarketPlaceClient marketPlaceClient;
    public MarketPlaceTrigger(MarketPlaceClient marketPlaceClient) {
        this.marketPlaceClient = marketPlaceClient;
    }

    @Override
    public void run() {
        marketPlaceClient.getConfiguration();
    }
}
