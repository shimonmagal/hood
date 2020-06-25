package com.hood.server.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.hood.server.api.auth.Secured;
import com.hood.server.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bson.Document;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.hood.server.services.DBInterface;
import com.hood.server.model.Flyer;

@Secured
@Path("flyers")
public class FlyersApi
{
	private static final Logger logger = LoggerFactory.getLogger(FlyersApi.class);
	
	@GET
	public Response getFlyters(
		@QueryParam("longitude") double longitude, 
		@QueryParam("latitude") double latitude,
		@QueryParam("maxDistanceInMetters") double maxDistanceInMetters)
	{
		try
		{
			logger.info("Getting all flyers for (long: {}) (lat: {}) (distance: {})", 
					longitude, latitude, maxDistanceInMetters);
			
			List<Document> documents = DBInterface.get().getNearestDocuments(
					Flyer.ENTITY_PLURAL_NAME, Flyer.LOCATION_FIELD_NAME, longitude, latitude, maxDistanceInMetters, 0);
			
			if (documents == null)
			{
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			
			List<Flyer> flyers = documents.stream()
				.map(doc -> Flyer.fromBsonObject(doc))
				.filter(flyer -> flyer != null)
				.collect(Collectors.toList());
			
			ObjectMapper jsonMapper = new ObjectMapper(); 
			String resultJson = jsonMapper.writeValueAsString(flyers);
			
			return Response
				.status(Response.Status.OK)
				.entity(resultJson)
				.build();
		}
		catch (Exception e)
		{
			logger.error("Error getting flyers", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	public Response addFlyer(String flyerJson, @Context ContainerRequestContext requestContext)
	{
		String authenticatedEmail = requestContext.getProperty("authenticatedEmail").toString();
		
		if (authenticatedEmail == null)
		{
			logger.error("addFlyer: authenticatedEmail is null");
			return Response.status(403).build();
		}
		
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper(); 
			Flyer.Builder flyerBuilder = jsonMapper.readValue(flyerJson, Flyer.Builder.class);
			flyerBuilder.withUser(authenticatedEmail);
			Flyer flyer = flyerBuilder.build();
			
			logger.info("Adding new flyer: {}", flyer);
			
			if (!DBInterface.get().addDocument(Flyer.ENTITY_PLURAL_NAME, flyer.toBsonObject()))
			{
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			
			return Response
				.status(Response.Status.OK)
				.build();
		}
		catch (Exception e)
		{
			logger.error("Error adding flyer {}", flyerJson, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}	
	}
	
	public static boolean validateFlyerIsByUser(String flyerId, String user)
	{
		try
		{
			logger.info("Validating flyer: {} belongs to user: {}", flyerId, user);
			
			
			Map<String, DBInterface.OperatorAndValue> fieldToOperatorAndValue = Maps.newHashMap();
			fieldToOperatorAndValue.put(Flyer.USER_FIELD_NAME, DBInterface.OperatorAndValue.of(DBInterface.Operator.EQUALS, user));
			fieldToOperatorAndValue.put(Flyer.FLYER_ID_FIELD, DBInterface.OperatorAndValue.of(DBInterface.Operator.EQUALS, flyerId));
			
			List<Document> documents = DBInterface.get().query(Flyer.ENTITY_PLURAL_NAME, DBInterface.LogicalOperator.AND,
					fieldToOperatorAndValue);
			
			if (documents == null || documents.size() == 0)
			{
				logger.error("User: {} is not owner of flyer: {}", user, flyerId);
				return false;
			}
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error occured while validateFlyerIsByUser", e);
			return false;
		}
	}
}
