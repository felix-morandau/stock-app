package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.config.UserNotFoundException;
import com.felix_morandau.stock_app.dto.user.CreateUserDTO;
import com.felix_morandau.stock_app.dto.login.LoginRequest;
import com.felix_morandau.stock_app.dto.login.LoginResponse;
import com.felix_morandau.stock_app.dto.user.UpdateUserDTO;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.entity.enums.UserType;
import com.felix_morandau.stock_app.entity.transactional.UserPreferredSettings;
import com.felix_morandau.stock_app.repository.PortfolioRepository;
import com.felix_morandau.stock_app.repository.SettingsRepository;
import com.felix_morandau.stock_app.repository.UserRepository;
import com.felix_morandau.stock_app.util.JwtUtil;
import com.felix_morandau.stock_app.util.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public List<User> getUsers(UserType userType) {

        if (userType == null) {
            return userRepository.findAll();
        }

        return userRepository.findUsersByType(userType);
    }

    public User getUser(UUID userId) {

        return userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public User addUser(CreateUserDTO dto) {
        User newUser = new User();

        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordUtil.hashPassword(dto.getPassword()));
        newUser.setAge(dto.getAge());
        newUser.setType(dto.getType());
        newUser.setCountry(dto.getCountry());
        newUser.setBio(dto.getBio());
        newUser.setBalance(1000000000);

        UserPreferredSettings settings = new UserPreferredSettings();
        newUser.setUserPreferredSettings(settingsRepository.save(settings));

        User savedUser = userRepository.save(newUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setProfit(0.0);
        portfolio.setTotalInvested(0.0);
        portfolio.setUnrealizedPnl(0.0);
        portfolio.setReturnPercentage(0.0);
        portfolio.setDefaultName(savedUser.getFirstName() + " " + savedUser.getLastName());
        portfolio = portfolioRepository.save(portfolio);

        savedUser.getPortfolios().add(portfolio);
        return userRepository.save(savedUser);
    }

    public User updateUser(UpdateUserDTO dto, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + userId + " not found"));

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName().trim());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null) {

            user.setEmail(dto.getEmail().trim().toLowerCase());
        }

        if (dto.getPassword() != null) {
            user.setPassword(passwordUtil.hashPassword(dto.getPassword()));
        }

        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }

        if (dto.getCountry() != null) {
            user.setCountry(dto.getCountry());
        }

        if (dto.getBio() != null) {
            user.setBio(dto.getBio().trim());
        }

        if (dto.getUserPreferredSettings() != null) {
            UserPreferredSettings incoming = dto.getUserPreferredSettings();
            UserPreferredSettings current = user.getUserPreferredSettings();

            if (incoming.getLanguage() != null)
                current.setLanguage(incoming.getLanguage());

            if (incoming.getTheme() != null)
                current.setTheme(incoming.getTheme());

            if (incoming.getCurrency() != null)
                current.setCurrency(incoming.getCurrency());
        }

        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + "not found"));

        userRepository.delete(user);
    }

    public User getUserByPortfolioId(UUID portfolioId) {
        return userRepository.findUserByPortfolioId(portfolioId)
                .orElseThrow(() -> new IllegalStateException("User not found for portfolio: " + portfolioId));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findUserByEmail(request.email());

        if (user.isEmpty()) {
            return new LoginResponse(false, null, null, "User with email " + request.email() + " not found", null);
        }

        String token =jwtUtil.generateToken(user.get());

        if (passwordUtil.checkPassword(request.password(), user.get().getPassword())) {
            return new LoginResponse(true, user.get().getId(), user.get().getType(), null, token);
        } else {
            return new LoginResponse(false, null, null, "Incorrect password", null);
        }
    }
}
