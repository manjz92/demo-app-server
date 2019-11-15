package com.example.errorhandler;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.example.builder.ErrorMessage;
import com.example.builder.ErrorMessageBuilder;

@Provider
@Component
public class ErrorHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {

		Response response;
		ErrorMessage message = ErrorMessageBuilder.statusCode(Status.BAD_REQUEST).message(exception.getMessage())
				.developerMsg(exception.toString()).exception(exception.getClass().getName()).build();
		response = Response.status(message.getStatusCode()).entity(message).build();
		return response;
	}

}
