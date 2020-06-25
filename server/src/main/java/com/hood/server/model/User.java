package com.hood.server.model;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User
{
	private static final Logger logger = LoggerFactory.getLogger(User.class);
	
	public static final String ENTITY_PLURAL_NAME = "users";
	public static final String USERNAME_FIELD = "email";
	public static final String PICTURE_FIELD = "picture";
	
	private final String email;
	private final String picture;
	
	public User(String email, String picture)
	{
		this.email = email;
		this.picture = picture;
	}
	
	public User(String email)
	{
		this(email, null);
	}
	
	public static User fromBsonDocument(Document bson)
	{
		String email = bson.getString("email");
		String picture = bson.getString("picture");
		
		return new User(email, picture);
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (email != null)
		{
			bson.append("email", email);
		}
		
		if (picture != null)
		{
			bson.append("picture", picture);
		}
		
		return bson;
	}
	
	@Override
	public String toString()
	{
		return String.format("(email: %s) (picture: %s)", email, picture);
	}
}
