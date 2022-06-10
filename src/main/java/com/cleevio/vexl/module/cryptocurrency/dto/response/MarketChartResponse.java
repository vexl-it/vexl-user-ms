package com.cleevio.vexl.module.cryptocurrency.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MarketChartResponse(

        @JsonProperty("prices")
        List<List<BigDecimal>> prices

) {
}
