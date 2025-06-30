package com.example.erv.dataimport;

import com.example.erv.dto.UserCsvDTO;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Service
public class UserCsvImportService {
    public List<UserCsvDTO> parseCsv(MultipartFile file) throws IOException, UserImportException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CsvToBean<UserCsvDTO> csvToBean = new CsvToBeanBuilder<UserCsvDTO>(reader)
                    .withType(UserCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(true)
                    .build();

            List<UserCsvDTO> users = csvToBean.parse();
            validateCSV(users);
            return users;
        }
    }

    private static void validateCSV(List<UserCsvDTO> users) throws UserImportException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        for (UserCsvDTO user : users) {
            Set<ConstraintViolation<UserCsvDTO>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                throw new UserImportException("Invalid .csv data!");
            }
        }
    }
}
