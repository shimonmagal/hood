package com.hood.server.api.auth;

import com.hood.server.services.DBInterface;
import com.hood.server.session.SessionManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
	private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException
	{
		String session = requestContext.getHeaders().getFirst("session");
		
		if (session == null)
		{
			requestContext.abortWith(Response.status(403).build());
			return;
		}
		
		String email = SessionManager.get(session);
		
		if (email == null)
		{
			requestContext.abortWith(Response.status(403).build());
			return;
		}
		
		requestContext.setProperty("authenticatedSession", session);
		requestContext.setProperty("authenticatedEmail", email);
	}
}