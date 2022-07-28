package com.cleevio.vexl.module.cryptocurrency.service;

import com.cleevio.vexl.common.integration.coingecko.CoingeckoConnector;
import com.cleevio.vexl.common.integration.coingecko.dto.response.CoingeckoResponse;
import com.cleevio.vexl.common.integration.coingecko.dto.response.CoingeckoMarketResponse;
import com.cleevio.vexl.module.cryptocurrency.exception.CoinException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for getting information about cryptocurrency.
 */
@Service
@RequiredArgsConstructor
public class CryptocurrencyService {

    private final CoingeckoConnector coingeckoConnector;

    /**
     * Retrieve coin price from Coingecko. As parameter must be supported name of cryptocurrency.
     *
     * @param coin
     * @throws CoinException
     */
    public CoingeckoResponse retrieveCoinPrice(String coin) {
        return this.coingeckoConnector.retrieveCoinPrice(coin);
    }

    public CoingeckoMarketResponse retrieveMarketChart(String from, String to, String currency) {
        return this.coingeckoConnector.retrieveMarketChart(from, to, currency);
    }
}
