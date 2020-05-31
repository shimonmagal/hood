package com.hood.server.api.auth;

import com.hood.server.model.Session;
import com.hood.server.model.User;
import com.hood.server.services.DBInterface;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Secured
@Path("session")
public class SessionApi
{
	private static final Logger logger = LoggerFactory.getLogger(SessionApi.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkSession(@Context ContainerRequestContext requestContext)
	{
		String authenticatedEmail = requestContext.getProperty("authenticatedEmail").toString();
		
		if (authenticatedEmail == null)
		{
			logger.error("checkSession: authenticatedEmail is null");
			return Response.status(403).build();
		}
		
		User user = new User(authenticatedEmail);
		
		Document userDoc = DBInterface.get().getDocument(User.ENTITY_PLURAL_NAME, user.toBsonObject());
		
		if (userDoc == null)
		{
			logger.error("Failed to retrieve user: {}", user);
			return Response.status(403).build();
		}
		
		logger.debug("Successfully fetched session for: {}", authenticatedEmail);
		return Response.ok().entity(userDoc.toJson()).build();
	}
	
	@Secured
	@DELETE
	public Response deleteSession(@Context ContainerRequestContext requestContext)
	{
		String authenticatedSession = requestContext.getProperty("authenticatedSession").toString();
		String authenticatedEmail = requestContext.getProperty("authenticatedEmail").toString();
		
		if (authenticatedSession == null)
		{
			logger.error("deleteSession: authenticatedSession is null");
		}
		
		Session session = new Session(authenticatedSession);
		
		if (!DBInterface.get().deleteDocument(Session.ENTITY_PLURAL_NAME, session.toBsonObject()))
		{
			logger.error("Failed to delete session: {}", session);
			return Response.status(403).build();
		}
		
		logger.debug("Successfully deleted session: {} (belongs to: {}", session, authenticatedEmail);
		return Response.ok().build();
	}
}
