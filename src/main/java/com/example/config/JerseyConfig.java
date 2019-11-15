package com.example.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.example.errorhandler.ErrorHandler;

@Component
@ApplicationPath(value = "api")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		packages("com.example.endpoint");
		register(ErrorHandler.class);

	}
}
