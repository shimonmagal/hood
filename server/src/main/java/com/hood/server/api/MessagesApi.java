package com.hood.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hood.server.model.Message;
import com.hood.server.services.DBInterface;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hood.server.services.DBInterface.LogicalOperator;
import static com.hood.server.services.DBInterface.Operator;
import static com.hood.server.services.DBInterface.OperatorAndValue;

@Path("messages")
public class MessagesApi
{
	private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response pushMessage(@QueryParam("flyerId") String flyerId,
	                            @QueryParam("receiverUser") String receiverUser,
	                            @QueryParam("date") String dateString,
	                            String text,
	                            @Context ContainerRequestContext requestContext)
	{
		Object authenticatedEmail = requestContext.getProperty("authenticatedEmail");
		
		if (authenticatedEmail == null)
		{
			logger.error("checkSession: authenticatedEmail is null");
			return Response.status(403).build();
		}
		
		String sender = authenticatedEmail.toString();
		
		Date date = new Date(Long.parseLong(dateString));
		
		Document message = new Message(flyerId, sender, receiverUser, date, text).toBsonObject();
		DBInterface.get().addDocument(Message.ENTITY_PLURAL_NAME, message);
		
		return Response.ok().build();
	}
	
	@GET
	public Response getAllMessage(@Context ContainerRequestContext requestContext)
	{
		Object authenticatedEmail = requestContext.getProperty("authenticatedEmail");
		
		if (authenticatedEmail == null)
		{
			logger.error("checkSession: authenticatedEmail is null");
			return Response.status(403).build();
		}
		
		String user = authenticatedEmail.toString();
		
		try
		{
			Map<String, OperatorAndValue> fieldToOperatorAndValue = Maps.newHashMap();
			
			fieldToOperatorAndValue.put("senderUser", OperatorAndValue.of(Operator.EQUALS, user));
			fieldToOperatorAndValue.put("receiverUser", OperatorAndValue.of(Operator.EQUALS, user));
			
			List<Document> documents = DBInterface.get().query(Message.ENTITY_PLURAL_NAME, LogicalOperator.OR, fieldToOperatorAndValue);
			
			if (documents == null)
			{
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			
			List<Message> messages = documents.stream()
					.map(doc -> Message.fromBsonObject(doc))
					.filter(message -> message != null)
					.collect(Collectors.toList());
			
			ObjectMapper jsonMapper = new ObjectMapper();
			String resultJson = jsonMapper.writeValueAsString(messages);
			
			return Response
					.status(Response.Status.OK)
					.entity(resultJson)
					.build();
		}
		catch (Exception e)
		{
			logger.error("Error getting message", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
