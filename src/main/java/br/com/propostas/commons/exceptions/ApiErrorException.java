package br.com.propostas.commons.exceptions;

import org.springframework.http.HttpStatus;

public class ApiErrorException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private String campo;
	private HttpStatus httpStatus;
	
	public ApiErrorException(String message, String campo, HttpStatus httpStatus) {
		super(message);
		this.campo = campo;
		this.httpStatus = httpStatus;
	}

	public String getCampo() {
		return campo;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	
}
