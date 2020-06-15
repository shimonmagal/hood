package com.hood.server.api;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.Provider;

@Provider
public class LogAllExceptions implements ExtendedExceptionMapper<Throwable> {
	
	private static final Logger logger = LoggerFactory.getLogger(LogAllExceptions.class);
	
	@Override
	public boolean isMappable(Throwable thro) {
		if (isServerError(thro))
		{
			logger.error("ThrowableLogger_ExceptionMapper logging error.", thro);
		}
		else
		{
			logger.info("ThrowableLogger_ExceptionMapper logging error.", thro);
		}
		
		return false;
	}
	
	private boolean isServerError(Throwable thro) {
		return thro instanceof WebApplicationException
				&& isServerError((WebApplicationException)thro);
	}
	
	private boolean isServerError(WebApplicationException exc) {
		return exc.getResponse().getStatusInfo().getFamily().equals(Family.SERVER_ERROR);
	}
	
	@Override
	public Response toResponse(Throwable throwable) {
		//assert false;
		logger.error("ThrowableLogger_ExceptionMapper.toResponse: This should not have been called.");
		throw new RuntimeException("This should not have been called");
	}
	
}