package com.felix_morandau.stock_app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix_morandau.stock_app.dto.stocks.StockCandleDTO;
import com.felix_morandau.stock_app.dto.stocks.StockDataDTO;
import com.felix_morandau.stock_app.entity.enums.StockSymbol;
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

@Service
public class StockService {

    private static final String BASE_URL = "https://api.twelvedata.com";

    @Value("${twelve.key}")
    private String apiKey;

    public Double getCurrentPrice(StockSymbol symbol) {
        try {
            String url = BASE_URL + "/price?symbol=" + symbol.name() +
                    "&apikey=" + apiKey;

            String response = sendGetRequest(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("price")) {
                return root.path("price").asDouble();
            } else {
                throw new RuntimeException("Price not found in response: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StockDataDTO fetchDayData(StockSymbol symbol, LocalDate day) {
        try {
            String url = BASE_URL + "/time_series?symbol=" + symbol.name() +
                    "&interval=1h&outputsize=24&timezone=Etc/GMT&apikey=" + apiKey;

            String response = sendGetRequest(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode valuesNode = root.path("values");

            if (!valuesNode.isArray()) {
                throw new RuntimeException("Invalid response from API");
            }

            Map<LocalDateTime, StockCandleDTO> candles = new TreeMap<>();

            for (JsonNode node : valuesNode) {
                String datetimeStr = node.path("datetime").asText();
                LocalDateTime dateTime = LocalDateTime.parse(datetimeStr.replace(" ", "T"));

                if (dateTime.toLocalDate().equals(day)) {
                    double open = node.path("open").asDouble();
                    double close = node.path("close").asDouble();
                    double high = node.path("high").asDouble();
                    double low = node.path("low").asDouble();
                    long volume = node.path("volume").asLong();

                    candles.put(dateTime, new StockCandleDTO(open, close, high, low, volume));
                }
            }

            return new StockDataDTO(symbol.name(), candles);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StockDataDTO fetchMonthHistory(StockSymbol symbol) {
        try {
            String url = BASE_URL + "/time_series?symbol=" + symbol.name() +
                    "&interval=1day&outputsize=30&timezone=Etc/GMT&apikey=" + apiKey;

            String response = sendGetRequest(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode values = root.path("values");

            if (!values.isArray()) {
                throw new RuntimeException("Invalid response from Twelve Data API");
            }

            Map<LocalDateTime, StockCandleDTO> dailyCandles = new TreeMap<>();

            for (JsonNode node : values) {
                String date = node.get("datetime").asText();
                LocalDateTime dateTime = LocalDateTime.parse(date + "T00:00:00");

                double open = node.get("open").asDouble();
                double close = node.get("close").asDouble();
                double high = node.get("high").asDouble();
                double low = node.get("low").asDouble();
                long volume = node.get("volume").asLong();

                StockCandleDTO candle = new StockCandleDTO(open, close, high, low, volume);
                dailyCandles.put(dateTime, candle);
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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            return result.toString();
        } finally {
            conn.disconnect();
        }
    }
}
