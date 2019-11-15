package com.example.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.builder.SuccessMessage;
import com.example.builder.SuccessMessageBuilder;
import com.example.dto.CommentFormSaveDto;
import com.example.service.CommentFormService;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/comment-form")
public class CommentFormEndPoint {

	@Autowired
	private CommentFormService commentFormService;

	@POST
	@Path("/save")
	public SuccessMessage saveFormData(CommentFormSaveDto saveData) {

		return SuccessMessageBuilder.statusCode(Status.OK).message(commentFormService.saveFormData(saveData)).build();

	}

}
