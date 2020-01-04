package com.example.filedemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPathException extends RuntimeException {

	public InvalidPathException(String message) {
		super(message);
	}

	public InvalidPathException(String message, Throwable cause) {
		super(message, cause);
	}
}
