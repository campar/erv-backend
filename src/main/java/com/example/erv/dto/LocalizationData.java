package com.example.erv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationData {
	private String entityName;
	private String attributeName;
	private String value;
}
