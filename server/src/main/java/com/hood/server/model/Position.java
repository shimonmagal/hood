package com.hood.server.model;

import java.util.Arrays;

import org.bson.Document;
import org.bson.BsonArray;
import org.bson.BsonDouble;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Position.Builder.class)
public class Position
{
	private final double longitude;
	private final double latitude;
	
	public Position(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	@Override
	public String toString()
	{
		return String.format("(long: %f) (lat: %f)", 
			longitude, latitude);
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		bson.append("type", "Point");
		bson.append("coordinates", new BsonArray(Arrays.asList(new BsonDouble(longitude), new BsonDouble(latitude))));
		
		return bson;
	}
	
	@JsonPOJOBuilder
	public static class Builder
	{
		private double longitude;
		private double latitude;
		
		public Builder withLongitude(double longitude)
		{
			this.longitude = longitude;
			return this;
		}
		
		public Builder withLatitude(double latitude)
		{
			this.latitude = latitude;
			return this;
		}
		
		public Position build()
		{
			return new Position(longitude, latitude);
		}
	}
}
