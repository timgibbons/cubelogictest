package com.timgibbons.cubelogic.tradeanalyser.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private long id;
    private BigDecimal price;
    private double volume;
    private Side side;
    private LocalDateTime timestamp;
}




