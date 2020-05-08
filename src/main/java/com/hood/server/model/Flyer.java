package com.hood.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Flyer.Builder.class)
public class Flyer
{
	private final String title;
	private final String description;
	private final String imageUrl;
	
	public Flyer(String title, String description, String imageUrl)
	{
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getImageUrl()
	{
		return imageUrl;
	}
	
	public String toString()
	{
		return String.format("(title: %s) (description: %s) (image: %s)", title, description, imageUrl);
	}
	
	@JsonPOJOBuilder
	public static class Builder
	{
		private String title;
		private String description;
		private String imageUrl;
		
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
		
		public Builder withImageUrl(String imageUrl)
		{
			this.imageUrl = imageUrl;
			return this;
		}
		
		public Flyer build()
		{
			return new Flyer(title, description, imageUrl);
		}
	}
}
