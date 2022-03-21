package com.cleevio.vexl.module.cryptocurrency.service;

import com.cleevio.vexl.module.cryptocurrency.dto.response.CoingeckoResponse;
import com.cleevio.vexl.module.cryptocurrency.exception.CoinException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Service for getting information about cryptocurrency.
 */
@Service
@Slf4j
@AllArgsConstructor
public class CryptocurrencyService {

    @Value("${coingecko.url}")
    private String coingeckoUrl;

    @Value("${coingecko.url.coin}")
    private String coingeckoUrlCoinApi;

    /**
     * Retrieve coin price from Coingecko. As parameter must be supported name of cryptocurrency.
     *
     * @param coin
     * @throws CoinException
     */
    public CoingeckoResponse retrieveCoinPrice(String coin)
            throws CoinException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<CoingeckoResponse> coingeckoResponse = restTemplate.getForEntity(this.coingeckoUrl + this.coingeckoUrlCoinApi, CoingeckoResponse.class, coin);
            return Objects.requireNonNull(coingeckoResponse.getBody());
        } catch (Exception e) {
            log.error("Error occurred during retrieval of data from Coingecko", e);
            throw new CoinException();
        }

    }
}
