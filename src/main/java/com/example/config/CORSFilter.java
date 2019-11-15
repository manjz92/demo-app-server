package com.example.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CORSFilter implements Filter {

	static Logger logger = LoggerFactory.getLogger(CORSFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", "60");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept,Authorization,Access-Control-Allow-Origin");
		response.setHeader("Access-Control-Expose-Headers", "Authorization,Access-Control-Allow-Origin");
//		System.out.println("CORSFilter.doFilter()");
		/**
		 * Allowing request if first request is OPTIONS
		 */
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		chain.doFilter(req, res);

	}

	@Override
	public void destroy() {
		logger.info("destroy");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("init");
	}

}
