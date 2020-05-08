package com.hood.server.flyers;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bson.Document;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.hood.server.services.DBInterface;
import com.hood.server.model.Flyer;

@Path("flyers")
public class FlyersApi
{
	private static final Logger logger = LoggerFactory.getLogger(FlyersApi.class);
	
	@GET
	public Response getFlyters()
	{
		try
		{
			List<Document> documents = DBInterface.get().getAllDocuments("flyers");
			
			if (documents == null)
			{
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			
			List<Flyer> flyers = documents.stream()
				.map(doc -> Flyer.fromBsonObject(doc))
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
	public Response addFlyer(String flyerJson)
	{
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper(); 
			Flyer flyer = jsonMapper.readValue(flyerJson, Flyer.class); 
			
			System.out.println("Add flyer called " + flyer);
			
			return Response
				.status(Response.Status.OK)
				.build();
		}
		catch (Exception e)
		{
			logger.error("Error adding flyer", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}	
	}
}
