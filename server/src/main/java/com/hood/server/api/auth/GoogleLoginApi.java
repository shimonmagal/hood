package com.hood.server.api.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.hood.server.model.User;
import com.hood.server.services.DBInterface;
import com.hood.server.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Path("google")
public class GoogleLoginApi
{
	private static final String CLIENT_ID = "39117846579-13272mh9u2ld14da8vvib1v5rqlsei5i.apps.googleusercontent.com";
	
	private static final Logger logger = LoggerFactory.getLogger(GoogleLoginApi.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@QueryParam("id_token") String idTokenString)
	{
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
				.setAudience(Collections.singletonList(CLIENT_ID))
				.build();
		
		try
		{
			GoogleIdToken idToken = verifier.verify(idTokenString);
			
			if (idToken != null)
			{
				GoogleIdToken.Payload payload = idToken.getPayload();
				
				// Print user identifier
				String userId = payload.getSubject();
				
				String email = payload.getEmail();
				logger.debug("User ID: {}", email);
				
				User user = new User(email, payload.get("picture").toString());
				
				if (!DBInterface.get().addDocument("users", user.toBsonObject()))
				{
					logger.error("Adding user with email: {} retrieved from google failed", email);
					return Response.serverError().entity("Error occurred in server").build();
				}
				
				String session = SessionManager.createSession(email);
				
				return Response.ok().
						header("session", session).
						entity(payload.toString()).
						build();
			}
		}
		catch (Exception e)
		{
			logger.error("Google login failed", e);
			
			return Response.serverError().entity("Error occurred in server").build();
		}
		
		return Response.status(403).entity("User not permitted").build();
	}
}
