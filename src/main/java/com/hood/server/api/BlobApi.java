package com.hood.server.api;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.hood.server.services.BlobInterface;

@Path("file")
public class BlobApi
{
	private static final Logger logger = LoggerFactory.getLogger(BlobApi.class);
	
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload(@FormDataParam("file") InputStream inputStream)
	{
		try
		{
			String fileKey = BlobInterface.get().putNew(inputStream);
			
			if (fileKey == null)
			{
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			
			logger.info("File uploaded: {}", fileKey);
			
			return Response
				.status(Response.Status.OK)
				.entity(fileKey)
				.build();
		}
		catch (Exception e)
		{
			logger.error("Error uploading file", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
