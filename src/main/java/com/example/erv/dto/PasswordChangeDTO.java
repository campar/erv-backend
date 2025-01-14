package com.example.erv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDTO {
	@NotNull
	private Long id;
	@NotBlank
	private String currentPassword;
	@NotBlank
	@Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(
    	regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s])[A-Za-z\\d\\W]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String newPassword;
	@NotBlank
	@Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(
    	regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s])[A-Za-z\\d\\W]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String confirmPassword;
}
