package com.hood.server.api.auth;

import com.hood.server.model.User;
import com.hood.server.services.DBInterface;
import com.hood.server.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("emailpassword")
public class EmailPasswordLoginApi
{
	private final Logger logger = LoggerFactory.getLogger(EmailPasswordLoginApi.class);
	
	@GET
	public Response authenticate(@QueryParam("email") String email, @QueryParam("password") String password)
	{
		try
		{
			logger.info("Authenticating email: {} pass: {}", email, password.length());
			
			// Currently a hack to enable hoodev user to create a session
			//
			if (!("hoodevtest@gmail.com".equals(email)))
			{
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			
			User user = new User(email, "");

			if (!DBInterface.get().addDocument("users", user.toBsonObject()))
			{
				logger.error("Adding user with email: {} using email password failed.", email);
				return Response.serverError().entity("Error occurred in server").build();
			}
			
			String session = SessionManager.createSession(email);
			
			return Response.ok().
					header("session", session).
					build();
		}
		catch (Exception e)
		{
			logger.error("Email password login failed", e);
			
			return Response.serverError().entity("Error occurred in server").build();
		}
	}
}
