package com.cleevio.vexl.module.cryptocurrency.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketData {

    @JsonProperty("current_price")
    private CurrentPrice currentPrice;

    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;

    @JsonProperty("price_change_percentage_7d")
    private Double priceChangePercentage7d;

    @JsonProperty("price_change_percentage_14d")
    private Double priceChangePercentage14d;

    @JsonProperty("price_change_percentage_30d")
    private Double priceChangePercentage30d;

    @JsonProperty("price_change_percentage_60d")
    private Double priceChangePercentage60d;

    @JsonProperty("price_change_percentage_200d")
    private Double priceChangePercentage200d;

    @JsonProperty("price_change_percentage_1y")
    private Double priceChangePercentage1y;

    @JsonProperty("last_updated")
    private ZonedDateTime lastUpdated;

}


