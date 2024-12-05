package com.timgibbons.cubelogic.tradeanalyser.interfaces;

import java.util.List;

import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;

public interface TradeAnalyser {
    List<Trade> findSuspiciousTrades(List<Trade> trades, List<Order> orders);
}
