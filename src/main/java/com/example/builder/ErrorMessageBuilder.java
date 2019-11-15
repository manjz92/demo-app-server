package com.example.builder;

import java.util.Objects;

import javax.ws.rs.core.Response.Status;

/**
 * @author Shreekantha
 *
 */
public class ErrorMessageBuilder {

	private Status statusCode;
	private String message;
	private String developerMsg;
	private String exception;

	ErrorMessageBuilder(Status statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public static ErrorMessageBuilder statusCode(Status httpStatus) {
		return new ErrorMessageBuilder(httpStatus);
	}

	public ErrorMessageBuilder message(String message) {
		this.message = message;
		return this;
	}

	public ErrorMessageBuilder developerMsg(String developerMsg) {
		this.developerMsg = developerMsg;
		return this;
	}

	public ErrorMessageBuilder exception(String exception) {
		this.exception = exception;
		return this;
	}

	public ErrorMessage build() {
		Objects.requireNonNull(this.statusCode, "statusCode must not be null");
		return new ErrorMessage(this.statusCode, this.message, this.developerMsg, this.exception);
	}
}