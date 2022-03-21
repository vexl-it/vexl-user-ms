package com.cleevio.vexl.module.cryptocurrency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CoinPriceResponse {

    @Schema(description = "Price of coind in USD.")
    private final Double priceUsd;

    @Schema(description = "Percentage of price change in the last 24 hours.")
    private final Double priceChangePercentage24h;

    @Schema(description = "Percentage of price change in the last 7 days.")
    private final Double priceChangePercentage7d;

    @Schema(description = "Percentage of price change in the last 14 days.")
    private final Double priceChangePercentage14d;

    @Schema(description = "Percentage of price change in the last 30 days.")
    private final Double priceChangePercentage30d;

    @Schema(description = "Percentage of price change in the last 60 days.")
    private final Double priceChangePercentage60d;

    @Schema(description = "Percentage of price change in the last 200 days.")
    private final Double priceChangePercentage200d;

    @Schema(description = "Percentage of price change in the last 1 year.")
    private final Double priceChangePercentage1y;

    @Schema(description = "The data was last updated.")
    private final ZonedDateTime lastUpdated;

    public CoinPriceResponse(CoingeckoResponse coingeckoResponse) {
        this.priceUsd = coingeckoResponse.getMarketData().getCurrentPrice().getUsd();
        this.priceChangePercentage24h = coingeckoResponse.getMarketData().getPriceChangePercentage24h();
        this.priceChangePercentage7d = coingeckoResponse.getMarketData().getPriceChangePercentage7d();
        this.priceChangePercentage14d = coingeckoResponse.getMarketData().getPriceChangePercentage14d();
        this.priceChangePercentage30d = coingeckoResponse.getMarketData().getPriceChangePercentage30d();
        this.priceChangePercentage60d = coingeckoResponse.getMarketData().getPriceChangePercentage60d();
        this.priceChangePercentage200d = coingeckoResponse.getMarketData().getPriceChangePercentage200d();
        this.priceChangePercentage1y = coingeckoResponse.getMarketData().getPriceChangePercentage1y();
        this.lastUpdated = coingeckoResponse.getMarketData().getLastUpdated();
    }


}
