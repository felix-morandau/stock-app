// src/main/java/com/felix_morandau/stock_app/util/CurrencyConverterUtil.java
package com.felix_morandau.stock_app.util;

import com.felix_morandau.stock_app.dto.stocks.StockCandleDTO;
import com.felix_morandau.stock_app.dto.stocks.StockDataDTO;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class CurrencyConverterUtil {

    private static final Map<String, Double> MULTIPLIERS = Map.of(
            "USD", 1.00,
            "EUR", 0.93,
            "GBP", 0.81,
            "JPY", 144.36,
            "RON", 4.49
    );

    public static double getMultiplier(String currencyCode) {
        return MULTIPLIERS.getOrDefault(currencyCode, 1.0);
    }

    public static StockCandleDTO convertCandle(StockCandleDTO raw, double mul) {
        return new StockCandleDTO(
                raw.getOpen()  * mul,
                raw.getClose() * mul,
                raw.getHigh()  * mul,
                raw.getLow()   * mul,
                raw.getVolume()
        );
    }

    public static StockDataDTO convertAll(StockDataDTO rawDto, double mul) {
        Map<LocalDateTime, StockCandleDTO> converted = new TreeMap<>();

        rawDto.getPrices().forEach((dt, candle) ->
                converted.put(dt, convertCandle(candle, mul))
        );

        return new StockDataDTO(rawDto.getSymbol(), converted);
    }
}
