package com.hood.server.model;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Session
{
	private static final Logger logger = LoggerFactory.getLogger(Session.class);
	
	public static final String ENTITY_PLURAL_NAME = "sessions";
	
	private final String session;
	private final String email;
	private final Date lastLoginTime;
	
	public Session(String session, String email, Date lastLoginTime)
	{
		this.session = session;
		this.email = email;
		this.lastLoginTime = lastLoginTime;
	}
	
	public Session(String session)
	{
		this(session, null, null);
	}
	
	public static Session fromBsonDocument(Document bson)
	{
		String session = bson.getString("session");
		String email = bson.getString("email");
		Date lastLoginDate = bson.getDate("lastLoginDate");
		
		return new Session(session, email, lastLoginDate);
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (session != null)
		{
			bson.append("session", session);
		}
		
		if (email != null)
		{
			bson.append("email", email);
		}
		
		if (lastLoginTime != null)
		{
			bson.append("lastLoginTime", lastLoginTime);
		}
		
		
		return bson;
	}
	
	@Override
	public String toString()
	{
		return String.format("(session: %s) (email: %s)", session, email);
	}
	
	public String getEmail()
	{
		return email;
	}
}
