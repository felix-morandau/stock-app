package com.felix_morandau.stock_app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix_morandau.stock_app.dto.stocks.StockCandleDTO;
import com.felix_morandau.stock_app.dto.stocks.StockDataDTO;
import com.felix_morandau.stock_app.entity.enums.StockSymbol;
import com.felix_morandau.stock_app.entity.enums.CurrencyType;
import com.felix_morandau.stock_app.util.CurrencyConverterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final String BASE_URL = "https://api.twelvedata.com";

    @Value("${twelve.key}")
    private String apiKey;

    private final SettingsService settingsService;

    public Double getCurrentPrice(StockSymbol symbol, UUID userId) {
        try {
            // fetch user’s preferred currency and conversion multiplier
            CurrencyType currency = settingsService.getUserSettings(userId).getCurrency();
            double mul = CurrencyConverterUtil.getMultiplier(currency.name());

            // raw price in USD
            String url = BASE_URL + "/price?symbol=" + symbol.name()
                    + "&apikey=" + apiKey;
            JsonNode root = new ObjectMapper().readTree(sendGetRequest(url));
            if (!root.has("price")) {
                throw new RuntimeException("Price not found: " + root.toString());
            }
            double raw = root.path("price").asDouble();
            return raw * mul;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StockDataDTO fetchDayData(StockSymbol symbol, LocalDate day, UUID userId) {
        try {
            CurrencyType currency = settingsService.getUserSettings(userId).getCurrency();
            double mul = CurrencyConverterUtil.getMultiplier(currency.name());

            String url = BASE_URL + "/time_series?symbol=" + symbol.name()
                    + "&interval=1h&outputsize=24"
                    + "&timezone=Etc/GMT"
                    + "&apikey=" + apiKey;
            JsonNode root = new ObjectMapper().readTree(sendGetRequest(url));
            JsonNode values = root.path("values");
            if (!values.isArray()) {
                throw new RuntimeException("Invalid response from API");
            }

            Map<LocalDateTime, StockCandleDTO> candles = new TreeMap<>();
            for (JsonNode node : values) {
                LocalDateTime dt = LocalDateTime
                        .parse(node.path("datetime").asText().replace(" ", "T"));
                if (dt.toLocalDate().equals(day)) {
                    candles.put(dt, new StockCandleDTO(
                            node.path("open").asDouble()  * mul,
                            node.path("close").asDouble() * mul,
                            node.path("high").asDouble()  * mul,
                            node.path("low").asDouble()   * mul,
                            node.path("volume").asLong()
                    ));
                }
            }
            return new StockDataDTO(symbol.name(), candles);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StockDataDTO fetchMonthHistory(StockSymbol symbol, UUID userId) {
        try {
            CurrencyType currency = settingsService.getUserSettings(userId).getCurrency();
            double mul = CurrencyConverterUtil.getMultiplier(currency.name());

            String url = BASE_URL + "/time_series?symbol=" + symbol.name()
                    + "&interval=1day&outputsize=30"
                    + "&timezone=Etc/GMT"
                    + "&apikey=" + apiKey;
            JsonNode root = new ObjectMapper().readTree(sendGetRequest(url));
            JsonNode values = root.path("values");
            if (!values.isArray()) {
                throw new RuntimeException("Invalid response from API");
            }

            Map<LocalDateTime, StockCandleDTO> dailyCandles = new TreeMap<>();
            for (JsonNode node : values) {
                LocalDateTime dt = LocalDateTime
                        .parse(node.get("datetime").asText() + "T00:00:00");
                dailyCandles.put(dt, new StockCandleDTO(
                        node.get("open").asDouble()  * mul,
                        node.get("close").asDouble() * mul,
                        node.get("high").asDouble()  * mul,
                        node.get("low").asDouble()   * mul,
                        node.get("volume").asLong()
                ));
            }
            return new StockDataDTO(symbol.name(), dailyCandles);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String sendGetRequest(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
}
