package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.config.PortfolioNotFoundException;
import com.felix_morandau.stock_app.config.UserNotFoundException;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.repository.PortfolioRepository;
import com.felix_morandau.stock_app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    protected Portfolio addDefaultPortfolio(UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Portfolio portfolio = new Portfolio();
        portfolio.setDefaultName(user.getFirstName());
        portfolio.setAmount(0.0f);

        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getUserPortfolios(UUID userId) {

        return portfolioRepository.findPortfoliosByUser(userId);
    }

    public Portfolio addPortfolio(UUID userId, String name) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Portfolio portfolio = new Portfolio();
        portfolio.setName(name);
        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        portfolioRepository.delete(portfolio);
    }
}

