package com.timgibbons.cubelogic.tradeanalyser.services;

import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Side;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TradeAnalyserImplTest {

    TradeAnalyserImpl tradeAnalyser;

    @BeforeEach
    void init() {
        tradeAnalyser = new TradeAnalyserImpl();
    }

    @Test
    void testFindSuspiciousTradesTradeWindow() {

        // Given
        var trade1 = new Trade(1, new BigDecimal("100.0"), 10.0, Side.BUY,
                LocalDateTime.now());
        // Sell order within trade window (30 Min) (not suspicious)
        var order1 = new Order(1, new BigDecimal("100.0"), 5.0, Side.SELL,
                LocalDateTime.now().minusMinutes(20));
        // Sell order at trade window (30 Min) (suspicious)
        var order2 = new Order(1, new BigDecimal("100.0"), 5.0, Side.SELL,
                LocalDateTime.now().minusMinutes(30));
        // Sell order outside trade window (30 Min) (suspicious)
        var order3 = new Order(3, new BigDecimal("100.0"), 5.0, Side.SELL,
                LocalDateTime.now().minusMinutes(40));

        // When
        var suspiciousTrades = tradeAnalyser.findSuspiciousTrades(
                List.of(trade1),
                List.of(order1, order2, order3)
        );

        // Then
        assertEquals(1, suspiciousTrades.size());
    }

    @Test
    void testFindSuspiciousTradesPriceRange() {

        // Given
        var trade1 = new Trade(1, new BigDecimal("100.00"), 10.0, Side.BUY, LocalDateTime.now());
        // Sell order within 10% range (not suspicious)
        var order1 = new Order(1, new BigDecimal("109.00"), 5.0, Side.SELL, LocalDateTime.now());
        // Sell order exactly 10% higher (now suspicious)
        var order2 = new Order(2, new BigDecimal("110.00"), 5.0, Side.SELL, LocalDateTime.now());
        // Sell order more than 10% higher (still suspicious)
        var order3 = new Order(3, new BigDecimal("115.00"), 5.0, Side.SELL, LocalDateTime.now());

        // When
        var suspiciousTrades = tradeAnalyser.findSuspiciousTrades(
                List.of(trade1),
                List.of(order1, order2, order3)
        );

        // Then
        assertEquals(1, suspiciousTrades.size());
    }

    @Test
    void testIsPriceSuspiciousBuyBuy() {
        // Given
        var trade = new Trade(1, new BigDecimal("101.0"), 10.0, Side.BUY,
                LocalDateTime.now());
        var order = new Order(1, new BigDecimal("105.0"), 5.0, Side.BUY,
                LocalDateTime.now());

        // When / Then
        assertFalse(tradeAnalyser.isPriceSuspicious(trade, order));
    }

    @Test
    void testIsTradeBeforePermittedWindow() {

        // Given
        var trade = new Trade(1, new BigDecimal("100.0"), 10.0, Side.BUY,
                LocalDateTime.now());
        var order = new Order(3, new BigDecimal("90.0"), 5.0, Side.SELL,
                LocalDateTime.now().minusMinutes(40));

        // When / Then
        assertTrue(tradeAnalyser.isTradeBeforePermittedWindow(trade, order));
    }

    @Test
    void testIsPriceSuspiciousBuyTrade() {
        // Given
        var buyTrade = new Trade(1, new BigDecimal("100.00"), 10.0, Side.BUY, null);
        // Sell order within 10% range (not suspicious)
        var withinRangeSellOrder = new Order(1, new BigDecimal("109.00"), 5.0, Side.SELL, null);
        // Sell order exactly 10% higher (now suspicious)
        var boundarySellOrder = new Order(2, new BigDecimal("110.00"), 5.0, Side.SELL, null);
        // Sell order more than 10% higher (still suspicious)
        var beyondSellOrder = new Order(3, new BigDecimal("115.00"), 5.0, Side.SELL, null);
        
        // When / Then
        assertFalse(tradeAnalyser.isPriceSuspicious(buyTrade, withinRangeSellOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(buyTrade, boundarySellOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(buyTrade, beyondSellOrder));
    }

    @Test
    void testIsPriceSuspiciousSellTrade() {
        // Given
        var sellTrade = new Trade(2, new BigDecimal("100.00"), 10.0, Side.SELL, null);
        // Buy order within 10% range (not suspicious)
        var withinRangeBuyOrder = new Order(4, new BigDecimal("91.00"), 5.0, Side.BUY, null);
        // Buy order exactly 10% lower (now suspicious)
        var boundaryBuyOrder = new Order(5, new BigDecimal("90.00"), 5.0, Side.BUY, null);
        // Buy order more than 10% lower (still suspicious)
        var beyondBuyOrder = new Order(6, new BigDecimal("85.00"), 5.0, Side.BUY, null);
        
        // When / Then
        assertFalse(tradeAnalyser.isPriceSuspicious(sellTrade, withinRangeBuyOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(sellTrade, boundaryBuyOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(sellTrade, beyondBuyOrder));
    }

    @Test
    void testIsPriceSuspiciousWithNegativeBuyPricing() {
        // Given
        // Negative price for a BUY trade
        var buyTrade = new Trade(1, new BigDecimal("-100.00"), 10.0, Side.BUY, null);
        // Negative price sell order within 10% range (not suspicious)
        var withinRangeSellOrder = new Order(1, new BigDecimal("-105.00"), 5.0, Side.SELL, null);
        // Negative price sell order exactly 10% higher (now suspicious)
        var boundarySellOrder = new Order(2, new BigDecimal("-110.00"), 5.0, Side.SELL, null);
        // Negative price sell order more than 10% higher (suspicious)
        var beyondSellOrder = new Order(3, new BigDecimal("-115.00"), 5.0, Side.SELL, null);
        
        // When / Then
        assertFalse(tradeAnalyser.isPriceSuspicious(buyTrade, withinRangeSellOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(buyTrade, boundarySellOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(buyTrade, beyondSellOrder));
    }

    @Test
    void testIsPriceSuspiciousWithNegativeSellPricing() {
        // Given
        // Negative price for a SELL trade
        var sellTrade = new Trade(2, new BigDecimal("-100.00"), 10.0, Side.SELL, null);
        // Negative price buy order within 10% range (not suspicious)
        var withinRangeBuyOrder = new Order(4, new BigDecimal("-95.00"), 5.0, Side.BUY, null);
        // Negative price buy order exactly 10% lower (now suspicious)
        var boundaryBuyOrder = new Order(5, new BigDecimal("-90.00"), 5.0, Side.BUY, null);
        // Negative price buy order more than 10% lower (suspicious)
        var beyondBuyOrder = new Order(6, new BigDecimal("-85.00"), 5.0, Side.BUY, null);

        // When / Then
        assertTrue(tradeAnalyser.isPriceSuspicious(sellTrade, beyondBuyOrder));
        assertFalse(tradeAnalyser.isPriceSuspicious(sellTrade, withinRangeBuyOrder));
        assertTrue(tradeAnalyser.isPriceSuspicious(sellTrade, boundaryBuyOrder));
    }


}