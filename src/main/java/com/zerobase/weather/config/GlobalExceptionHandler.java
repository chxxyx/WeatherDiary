package com.zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // == 지금 서버 내부에 문제가 있다
	@ExceptionHandler(Exception.class)
	public Exception handlerAllException() {
		System.out.println("error from GlobalExceptionHandler");

		return new  Exception();
	}

}
