package com.timgibbons.cubelogic.tradeanalyser.services;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Side;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;
import com.timgibbons.cubelogic.tradeanalyser.interfaces.TradeAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeAnalyserImpl implements TradeAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(TradeAnalyserImpl.class);

    private static final int SUSPICIOUS_TRADE_WINDOW = 30;
    
    @Override
    public List<Trade> findSuspiciousTrades(List<Trade> trades, List<Order> orders) {
        // Set used to ensure no duplicates when price and window out of bounds
        var suspiciousTrades = new HashSet<Trade>();

        logger.info("Checking for suspicious trades");

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

        if(!suspiciousTrades.isEmpty()) {
            logger.warn("Suspicious trades found: {}", suspiciousTrades.size());
        }

        return suspiciousTrades.stream().toList();
    }

    @VisibleForTesting
    boolean isTradeBeforePermittedWindow(Trade trade, Order order) {
        var tradeTimestamp = trade.getTimestamp();
        var orderTimestamp = order.getTimestamp();

        logger.info("Checking if order price is suspicious: Trade Timestamp = {}, Order Timestamp = {}", tradeTimestamp, orderTimestamp);
        var isSuspicious = orderTimestamp
                .isBefore(tradeTimestamp.minusMinutes(SUSPICIOUS_TRADE_WINDOW));

        if (isSuspicious) {
            logger.warn("Suspicious order detected on time window: Trade = {}, Order = {}", trade, order);
        }
        return isSuspicious;
    }

    @VisibleForTesting
    boolean isPriceSuspicious(Trade trade, Order order) {
        var tradePrice = trade.getPrice();
        var orderPrice = order.getPrice();

        logger.info("Checking if order price is suspicious: Trade Price = {}, Order Price = {}", tradePrice, orderPrice);

        var isSuspicious = switch (trade.getSide()) {
            case SELL -> {
                // Opposite side must be BUY
                if (order.getSide() == Side.SELL) {
                    logger.debug("Order is of the same side, skipping suspicious check.");
                    yield false;
                }

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
                if (order.getSide() == Side.BUY) {
                    logger.debug("Order is of the same side, skipping suspicious check.");
                    yield false;
                }

                // Sell orders 10% or more expensive are suspicious
                var thresholdPrice = tradePrice.multiply(BigDecimal.valueOf(1.10));

                // Account for negative pricing
                if (orderPrice.compareTo(BigDecimal.ZERO) >= 0)
                    yield orderPrice.compareTo(thresholdPrice) >= 0;
                else
                    yield orderPrice.compareTo(thresholdPrice) <= 0;
            }
        };

        if (isSuspicious) {
            logger.warn("Suspicious order detected on price: Trade = {}, Order = {}", trade, order);
        }
        return isSuspicious;
    }

}
