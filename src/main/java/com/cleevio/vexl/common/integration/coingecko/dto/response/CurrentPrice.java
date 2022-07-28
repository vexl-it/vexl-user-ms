package com.cleevio.vexl.common.integration.coingecko.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentPrice {

    private Double usd;

    private Double czk;

    private Double eur;
}
