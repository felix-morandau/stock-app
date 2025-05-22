package com.felix_morandau.stock_app.config;

import java.util.UUID;

public class PortfolioNotFoundException extends RuntimeException{

    public PortfolioNotFoundException(UUID portfolioID) {
        super("Portfolio with id " + portfolioID + " was not found");
    }
}
