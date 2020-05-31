package com.hood.server.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hood.server.model.User;
import com.hood.server.services.DBInterface;
import com.hood.server.session.SessionManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("facebook")
public class FacebookLoginApi
{
	private final Logger logger = LoggerFactory.getLogger(FacebookLoginApi.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@QueryParam("access_token") String accessToken)
	{
		try
		{
			URL facebookGraphUrl = new URL("https://graph.facebook.com/v2.12/me?fields=name,picture,email&access_token=" + accessToken);
			HttpURLConnection conn = (HttpURLConnection) facebookGraphUrl.openConnection();
			conn.setDoOutput(true);
			
			conn.connect();
			
			int code = conn.getResponseCode();
			
			if (code != HttpURLConnection.HTTP_OK)
			{
				logger.error("Facebook graph api returned code {} for accessToken: {}", code, accessToken);
				
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			
			StringBuilder json = new StringBuilder();
			
			try (InputStream is = conn.getInputStream();
			     InputStreamReader isr = new InputStreamReader(is);
			     BufferedReader br = new BufferedReader(isr))
			{
				String line;
				
				while ((line = br.readLine()) != null) {
					json.append(line+"\n");
				}
			}
			
			final ObjectNode node = new ObjectMapper().readValue(json.toString(), ObjectNode.class);
			
			if (!node.has("email"))
			{
				logger.error("No email field in response from facebook login. Full json: {}", json);
				
				System.out.println("contentType: " + node.get("contentType"));
			}
			
			String email = node.get("email").asText();

			User user = new User(email, node.findValue("picture").get("data").get("url").asText());

			if (!DBInterface.get().addDocument("users", user.toBsonObject()))
			{
				logger.error("Adding user with email: {} retrieved from facebook failed", email);
				return Response.serverError().entity("Error occurred in server").build();
			}
			
			String session = SessionManager.createSession(email);
			
			return Response.ok().
					header("session", session).
					entity(json.toString()).
					build();
		}
		catch (Exception e)
		{
			logger.error("Facebook login failed", e);
			e.printStackTrace();
			
			return Response.serverError().entity("Error occurred in server").build();
		}
	}
}
