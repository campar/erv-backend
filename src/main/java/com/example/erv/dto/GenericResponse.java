package com.example.erv.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse {
	private String code;
	private String message;
	private Object data;

	public static GenericResponse getSuccessfulResponse(Object data) {
		GenericResponse response = new GenericResponse();
		response.code = "200";
		response.message = "Success";
		response.data = data;
		return response;
	}
	public static GenericResponse getSuccessfulResponse(Object data, String message) {
		GenericResponse response = new GenericResponse();
		response.code = "200";
		response.message = message;
		response.data = data;
		return response;
	}

	public static GenericResponse getErrorResponse(String errorMessage) {
		GenericResponse response = new GenericResponse();
		response.code = "500";
		response.message = errorMessage;
		response.data = "";
		return response;
	}

	public static GenericResponse getErrorResponse(String code, String errorMessage) {
		GenericResponse response = new GenericResponse();
		response.code = code;
		response.message = errorMessage;
		response.data = "";
		return response;
	}

}
