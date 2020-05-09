package com.hood.server.model;

import org.bson.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Flyer.Builder.class)
public class Flyer
{
	private final String id;
	private final String title;
	private final String description;
	private final String imageKey;
	
	public Flyer(String id, String title, String description, String imageKey)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.imageKey = imageKey;
	}
	
	public String getId()
	{
		return id;
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
	
	@Override
	public String toString()
	{
		return String.format("(id: %s) (title: %s) (description: %s) (image: %s)", 
			id, title, description, imageKey);
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (id != null)
		{
			bson.append("_id", id);
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
		
		return bson;
	}
	
	public static Flyer fromBsonObject(Document bson)
	{
		return new Builder()
			.withId(bson.getObjectId("_id").toString())
			.withTitle(bson.getString("title"))
			.withDescription(bson.getString("description"))
			.withImageKey(bson.getString("imageKey"))
			.build();
	}
	
	@JsonPOJOBuilder
	public static class Builder
	{
		private String id;
		private String title;
		private String description;
		private String imageKey;
		
		public Builder withId(String id)
		{
			this.id = id;
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
		
		public Flyer build()
		{
			return new Flyer(id, title, description, imageKey);
		}
	}
}
