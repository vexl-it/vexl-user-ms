package com.cleevio.vexl.module.cryptocurrency.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentPrice {

    @JsonProperty("usd")
    private Double usd;
}
