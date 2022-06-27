package com.cleevio.vexl.module.cryptocurrency.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentPrice {

    @JsonProperty("usd")
    private Double usd;

    @JsonProperty("czk")
    private Double czk;

    @JsonProperty("eur")
    private Double eur;
}
