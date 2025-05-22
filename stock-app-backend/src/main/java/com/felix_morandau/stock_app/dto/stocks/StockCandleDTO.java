package com.felix_morandau.stock_app.dto.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockCandleDTO {
    private double open;
    private double close;
    private double high;
    private double low;
    private long volume;
}
