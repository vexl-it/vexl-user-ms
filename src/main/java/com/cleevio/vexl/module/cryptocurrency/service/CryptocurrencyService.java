package com.cleevio.vexl.module.cryptocurrency.service;

import com.cleevio.vexl.common.integration.coingecko.CoingeckoConnector;
import com.cleevio.vexl.common.integration.coingecko.dto.response.CoingeckoResponse;
import com.cleevio.vexl.common.integration.coingecko.dto.response.CoingeckoMarketResponse;
import com.cleevio.vexl.module.cryptocurrency.constant.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.cleevio.vexl.common.config.AppConfig.COIN_PRICE;
import static com.cleevio.vexl.common.config.AppConfig.MARKET_CHART;

/**
 * Service for getting information about cryptocurrency.
 */
@Service
@RequiredArgsConstructor
public class CryptocurrencyService {

    private final CoingeckoConnector coingeckoConnector;

    @Cacheable(value = COIN_PRICE, sync = true)
    public CoingeckoResponse retrieveCoinPrice(String coin) {
        return this.coingeckoConnector.retrieveCoinPrice(coin);
    }

    @Cacheable(value = MARKET_CHART, sync = true)
    public CoingeckoMarketResponse retrieveMarketChart(Duration duration, String currency) {
        final String to = String.valueOf(Instant.now().getEpochSecond());
        final String from = String.valueOf(createFromUnixTimestamp(duration));

        return this.coingeckoConnector.retrieveMarketChart(from, to, currency);
    }

    private long createFromUnixTimestamp(Duration duration) {
        switch (duration) {
            case DAY -> {
                return LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case HOUR -> {
                return LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case WEEK -> {
                return LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case MONTH -> {
                return LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case YEAR -> {
                return LocalDateTime.now().minusYears(1).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case SIX_MONTHS -> {
                return LocalDateTime.now().minusMonths(6).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case THREE_MONTHS -> {
                return LocalDateTime.now().minusMonths(3).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            default -> throw new IllegalArgumentException("Unknown duration: " + duration);
        }
    }
}
