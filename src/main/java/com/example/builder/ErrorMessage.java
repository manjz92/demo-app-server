package com.example.builder;

import javax.ws.rs.core.Response.Status;

public class ErrorMessage {
	private Status statusCode;
	private String message;
	private String developerMsg;
	private String exception;

	public ErrorMessage(Status statusCode, String message, String developerMsg, String exception) {
		super();
		this.statusCode = statusCode;
		this.message = message;
		this.developerMsg = developerMsg;
		this.exception = exception;
	}

	public Status getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Status statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMsg() {
		return developerMsg;
	}

	public void setDeveloperMsg(String developerMsg) {
		this.developerMsg = developerMsg;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * @param httpStatus
	 *            the Status code to set
	 * @return httpStatus
	 */
	public static ErrorMessageBuilder statusCode(Status httpStatus) {
		return new ErrorMessageBuilder(httpStatus);
	}

}
