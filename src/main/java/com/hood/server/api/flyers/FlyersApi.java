package com.hood.server.flyers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.nio.file.StandardCopyOption;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.hood.server.model.Flyer;

@Path("flyers")
public class FlyersApi
{
	private static final Logger logger = LoggerFactory.getLogger(FlyersApi.class);
	
	@GET
	@Consumes("text/plain")
	public Response getFlyters()
	{
		try
		{
			List<Flyer> flyers = new ArrayList<Flyer>();
			
			flyers.add(new Flyer("Delicious cake", "Made by a bunch of titan robots", "https://i.pinimg.com/originals/c8/a9/8c/c8a98c21623f40cdb723f4b43b73bbc8.png"));
			flyers.add(new Flyer("Guitar lessons", "The best guitar lessons in the world, the teacher have a mustache", "https://media.gettyimages.com/photos/cowboy-man-with-guitar-mustache-and-cowboy-hat-picture-id140140339"));
			flyers.add(new Flyer("Lost cat", "White cat has gone since yesterday", "https://www.petsworld.in/blog/wp-content/uploads/2014/09/Ragdoll1.jpg"));
			
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
	public Response addFlyer()
	{
		System.out.println("Add flyer called");
		
		return Response
			.status(Response.Status.OK)
			.build();
	}
}
