package com.example.builder;

import java.util.Objects;

import javax.ws.rs.core.Response.Status;

public class SuccessMessageBuilder {

	private Status statusCode;
	private String message;

	public SuccessMessageBuilder(Status statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public static SuccessMessageBuilder statusCode(Status httpStatus) {
		return new SuccessMessageBuilder(httpStatus);
	}

	public SuccessMessageBuilder message(String message) {
		this.message = message;
		return this;
	}

	public SuccessMessage build() {
		Objects.requireNonNull(this.statusCode, "STATUS_CODE_MUST_NOT_BE_NULL");
		return new SuccessMessage(this.statusCode, this.message);
	}
}
