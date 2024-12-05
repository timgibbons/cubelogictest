package com.timgibbons.cubelogic.tradeanalyser.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Side;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;
import com.timgibbons.cubelogic.tradeanalyser.interfaces.TradeAnalyser;

public class TradeAnalyserImpl implements TradeAnalyser {

    private static final int SUSPICIOUS_TRADE_WINDOW = 30;
    
    @Override
    public List<Trade> findSuspiciousTrades(List<Trade> trades, List<Order> orders) {
        // Set used to ensure no duplicates when price and window out of bounds
        var suspiciousTrades = new HashSet<Trade>();

        for (Trade trade : trades) {
            orders.stream()
                    .filter(order -> order.getSide() != trade.getSide())
                    .filter(order -> isTradeBeforePermittedWindow(trade, order))
                    .forEach(order -> suspiciousTrades.add(trade));
        }

        for (Trade trade : trades) {
            orders.stream()
                    .filter(order -> order.getSide() != trade.getSide())
                    .filter(order -> isPriceSuspicious(trade, order))
                    .forEach(order -> suspiciousTrades.add(trade));
        }

        return suspiciousTrades.stream().toList();
    }

    public boolean isTradeBeforePermittedWindow(Trade trade, Order order) {
        return order.getTimestamp()
                .isBefore(trade.getTimestamp().minusMinutes(SUSPICIOUS_TRADE_WINDOW));
    }

    public boolean isPriceSuspicious(Trade trade, Order order) {
        var tradePrice = trade.getPrice();
        var orderPrice = order.getPrice();

        return switch (trade.getSide()) {
            case SELL -> {
                // Opposite side must be BUY
                if (order.getSide() == Side.SELL)
                    yield false;

                // Buy orders 10% or more cheaper are suspicious
                var thresholdPrice = tradePrice.multiply(BigDecimal.valueOf(0.90));

                // Account for negative pricing
                if (orderPrice.compareTo(BigDecimal.ZERO) >= 0)
                    yield orderPrice.compareTo(thresholdPrice) <= 0;
                else
                    yield orderPrice.compareTo(thresholdPrice) >= 0;
            }
            case BUY -> {
                // Opposite side must be SELL
                if (order.getSide() == Side.BUY)
                    yield false;

                // Sell orders 10% or more expensive are suspicious
                var thresholdPrice = tradePrice.multiply(BigDecimal.valueOf(1.10));

                // Account for negative pricing
                if (orderPrice.compareTo(BigDecimal.ZERO) >= 0)
                    yield orderPrice.compareTo(thresholdPrice) >= 0;
                else
                    yield orderPrice.compareTo(thresholdPrice) <= 0;
            }
        };
    }

    public boolean compareWithNegative(BigDecimal orderPrice, BigDecimal thresholdPrice) {
        if (orderPrice.compareTo(BigDecimal.ZERO) >= 0)
            return orderPrice.compareTo(thresholdPrice) <= 0;
        else
            return orderPrice.compareTo(thresholdPrice) > 0;
    }

}
