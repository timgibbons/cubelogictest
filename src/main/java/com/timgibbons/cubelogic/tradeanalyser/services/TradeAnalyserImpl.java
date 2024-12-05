package com.timgibbons.cubelogic.tradeanalyser.services;

import java.util.List;

import com.timgibbons.cubelogic.tradeanalyser.domain.Order;
import com.timgibbons.cubelogic.tradeanalyser.domain.Trade;
import com.timgibbons.cubelogic.tradeanalyser.interfaces.TradeAnalyser;

public class TradeAnalyserImpl implements TradeAnalyser {
    
    @Override
    public List<Trade> findSuspiciousTrades(List<Trade> trades, List<Order> orders) {
        return trades;
    }
}
