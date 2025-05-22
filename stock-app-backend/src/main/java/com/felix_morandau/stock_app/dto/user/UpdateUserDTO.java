package com.felix_morandau.stock_app.dto.user;

import com.felix_morandau.stock_app.entity.enums.Country;
import com.felix_morandau.stock_app.entity.transactional.UserPreferredSettings;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserDTO {
    private String firstName;

    private String lastName;

    private String email;

    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-z\\d!@#$%^&*()_+]{8,}$",
            message = "Password must contain at least: one uppercase character, one lowercase character, one digit, one special character"
    )
    private String password;

    @Min(16)
    private Integer age;

    private Country country;

    private String bio;

    private UserPreferredSettings userPreferredSettings;
}
