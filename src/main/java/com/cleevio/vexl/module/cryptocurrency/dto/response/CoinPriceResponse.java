package com.cleevio.vexl.module.cryptocurrency.dto.response;

import com.cleevio.vexl.common.integration.coingecko.dto.response.CoingeckoResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

public record CoinPriceResponse(

        @Schema(description = "Price of coin in USD.")
        Double priceUsd,

        @Schema(description = "Price of coin in CZK.")
        Double priceCzk,

        @Schema(description = "Price of coin in EUR.")
        Double priceEur,

        @Schema(description = "Percentage of price change in the last 24 hours.")
        Double priceChangePercentage24h,

        @Schema(description = "Percentage of price change in the last 7 days.")
        Double priceChangePercentage7d,

        @Schema(description = "Percentage of price change in the last 14 days.")
        Double priceChangePercentage14d,

        @Schema(description = "Percentage of price change in the last 30 days.")
        Double priceChangePercentage30d,

        @Schema(description = "Percentage of price change in the last 60 days.")
        Double priceChangePercentage60d,

        @Schema(description = "Percentage of price change in the last 200 days.")
        Double priceChangePercentage200d,

        @Schema(description = "Percentage of price change in the last 1 year.")
        Double priceChangePercentage1y,

        @Schema(description = "The data was last updated.")
        ZonedDateTime lastUpdated

) {

    public CoinPriceResponse(CoingeckoResponse coingeckoResponse) {
        this(
                coingeckoResponse.getMarketData().getCurrentPrice().getUsd(),
                coingeckoResponse.getMarketData().getCurrentPrice().getCzk(),
                coingeckoResponse.getMarketData().getCurrentPrice().getEur(),
                coingeckoResponse.getMarketData().getPriceChangePercentage24h(),
                coingeckoResponse.getMarketData().getPriceChangePercentage7d(),
                coingeckoResponse.getMarketData().getPriceChangePercentage14d(),
                coingeckoResponse.getMarketData().getPriceChangePercentage30d(),
                coingeckoResponse.getMarketData().getPriceChangePercentage60d(),
                coingeckoResponse.getMarketData().getPriceChangePercentage200d(),
                coingeckoResponse.getMarketData().getPriceChangePercentage1y(),
                coingeckoResponse.getMarketData().getLastUpdated()
        );
    }


}
