package com.timgibbons.cubelogic.tradeanalyser.services;

import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Side;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;
import com.timgibbons.cubelogic.tradeanalyser.interfaces.TradeAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TradeAnalyserImplTest {

    TradeAnalyser tradeAnalyser;

    @BeforeEach
    void init() {
        tradeAnalyser = new TradeAnalyserImpl();
    }

    @Test
    void findSuspiciousTrades() {

        // Given
        var trade1 = new Trade(
                1,
                new BigDecimal("100.0"),
                10.0,
                Side.BUY,
                LocalDateTime.now());
        var order1 = new Order(
                1,
                new BigDecimal("105.0"),
                5.0,
                Side.SELL,
                LocalDateTime.now().minusMinutes(20));
        var order2 = new Order(
                1,
                new BigDecimal("120.0"),
                5.0,
                Side.SELL,
                LocalDateTime.now().minusMinutes(20));
        var order3 = new Order(
                3,
                new BigDecimal("90.0"),
                5.0,
                Side.BUY,
                LocalDateTime.now().minusMinutes(40));

        // When
        var suspiciousTrades = tradeAnalyser.findSuspiciousTrades(
                List.of(trade1),
                List.of(order1, order2, order3)
        );

        assertEquals(1, suspiciousTrades.size());
    }
}