package com.hood.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bson.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Flyer.Builder.class)
public class Flyer
{
	private static final Logger logger = LoggerFactory.getLogger(Flyer.class);
	
	public static final String ENTITY_PLURAL_NAME = "flyers";
	public static final String LOCATION_FIELD_NAME = "location";
	public static final String USER_FIELD_NAME = "user";
	public static final String FLYER_ID_FIELD = "_id";
	public static final String IMAGE_KEY_FIELD = "imageKey";
	public static final String TITLE_FIELD = "title";
	
	private final String id;
	private final String user;
	private final String title;
	private final String description;
	private final String imageKey;
	private final Position location;
	
	public Flyer(String id, String user, String title, String description, String imageKey, Position location)
	{
		this.id = id;
		this.user = user;
		this.title = title;
		this.description = description;
		this.imageKey = imageKey;
		this.location = location;
	}
	
	@Override
	public String toString()
	{
		return String.format("(id: %s) (user: %s) (title: %s) (description: %s) (image: %s) (loc: %s)",
			id, user, title, description, imageKey, location);
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (id != null)
		{
			bson.append("id", id);
		}
		
		if (user != null)
		{
			bson.append("user", user);
		}
		
		if (title != null)
		{
			bson.append("title", title);
		}
		
		if (description != null)
		{
			bson.append("description", description);
		}
		
		if (imageKey != null)
		{
			bson.append("imageKey", imageKey);
		}
		
		if (location != null)
		{
			bson.append(LOCATION_FIELD_NAME, location.toBsonObject());
		}
		
		return bson;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getImageKey()
	{
		return imageKey;
	}
	
	public Position getLocation()
	{
		return location;
	}
	
	public static Flyer fromBsonObject(Document bson)
	{
		Object locationDoc = bson.get(LOCATION_FIELD_NAME);
		
		if (!(locationDoc instanceof Document))
		{
			logger.error("Bad location found");
			return null;
		}
		
		return new Builder()
			.withId(bson.getObjectId("_id").toString())
			.withUser(bson.getString("user"))
			.withTitle(bson.getString("title"))
			.withDescription(bson.getString("description"))
			.withImageKey(bson.getString("imageKey"))
			.withLocation(Position.fromBsonObject((Document) locationDoc))
			.build();
	}
	
	@JsonPOJOBuilder
	public static class Builder
	{
		private String id;
		private String user;
		private String title;
		private String description;
		private String imageKey;
		private Position location;
		
		public Builder withId(String id)
		{
			this.id = id;
			return this;
		}
		
		public Builder withUser(String user)
		{
			this.user = user;
			return this;
		}
		
		public Builder withTitle(String title)
		{
			this.title = title;
			return this;
		}
		
		public Builder withDescription(String description)
		{
			this.description = description;
			return this;
		}
		
		public Builder withImageKey(String imageKey)
		{
			this.imageKey = imageKey;
			return this;
		}
		
		public Builder withLocation(Position location)
		{
			this.location = location;
			return this;
		}
		
		public String getId()
		{
			return id;
		}
		
		public String getUser()
		{
			return user;
		}
		
		public String getTitle()
		{
			return title;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public String getImageKey()
		{
			return imageKey;
		}
		
		public Position getLocation()
		{
			return location;
		}
		
		public Flyer build()
		{
			return new Flyer(id, user, title, description, imageKey, location);
		}
	}
}
