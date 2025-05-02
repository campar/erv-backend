package com.example.erv.dto.settings;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLanguageDTO {
    @NotEmpty
    private String languageCode;
}
