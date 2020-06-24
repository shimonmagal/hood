package com.hood.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@JsonDeserialize(builder = Message.Builder.class)
public class Message
{
	private static final Logger logger = LoggerFactory.getLogger(Message.class);
	
	public static final String ENTITY_PLURAL_NAME = "messages";
	
	private final String flyerId;
	private final String customerUser;
	private final String senderUser;
	private final String receiverUser;
	private final Date date;
	private final String text;
	
	public Message(String flyerId, String customerUser, String senderUser, String receiverUser, Date date, String text)
	{
		this.flyerId = flyerId;
		this.customerUser = customerUser;
		this.senderUser = senderUser;
		this.receiverUser = receiverUser;
		this.date = date;
		this.text = text;
	}
	
	public static Message fromBsonObject(Document bson)
	{
		return new Message.Builder()
				.withFlyerId(bson.getString("flyerId"))
				.withCustomerUser(bson.getString("customerUser"))
				.withSenderUser(bson.getString("senderUser"))
				.withReceiverUser(bson.getString("receiverUser"))
				.withDate(bson.getDate("date"))
				.withText(bson.getString("text"))
				.build();
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (flyerId != null)
		{
			bson.append("flyerId", flyerId);
		}
		
		if (customerUser != null)
		{
			bson.append("customerUser", customerUser);
		}
		
		if (senderUser != null)
		{
			bson.append("senderUser", senderUser);
		}
		
		if (receiverUser != null)
		{
			bson.append("receiverUser", receiverUser);
		}
		
		if (date != null)
		{
			bson.append("date", date);
		}
		
		if (text != null)
		{
			bson.append("text", text);
		}
		
		return bson;
	}
	
	@Override
	public String toString()
	{
		return String.format("(flyerId: %s) (customerUser: %s) (senderUser: %s)  (receiverUser: %s) (date: %s) (text: %s)",
				flyerId, customerUser, senderUser, receiverUser, date, text);
	}
	
	
	public String getFlyerId()
	{
		return flyerId;
	}
	
	public String getCustomerUser()
	{
		return customerUser;
	}
	
	public String getSenderUser()
	{
		return senderUser;
	}
	
	public String getReceiverUser()
	{
		return receiverUser;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public String getText()
	{
		return text;
	}
	
	@JsonPOJOBuilder
	public static class Builder
	{
		private String flyerId;
		private String customerUser;
		private String senderUser;
		private String receiverUser;
		private Date date;
		private String text;
		
		public Message.Builder withFlyerId(String flyerId)
		{
			this.flyerId = flyerId;
			return this;
		}
		
		public Message.Builder withCustomerUser(String customerUser)
		{
			this.customerUser = customerUser;
			return this;
		}
		
		public Message.Builder withSenderUser(String senderUser)
		{
			this.senderUser = senderUser;
			return this;
		}
		
		public Message.Builder withReceiverUser(String receiverUser)
		{
			this.receiverUser = receiverUser;
			return this;
		}
		
		public Message.Builder withDate(Date date)
		{
			this.date = date;
			return this;
		}
		
		public Message.Builder withText(String text)
		{
			this.text = text;
			return this;
		}
		
		public String getFlyerId()
		{
			return flyerId;
		}
		
		public String getCustomerUser()
		{
			return customerUser;
		}
		
		public String getSenderUser()
		{
			return senderUser;
		}
		
		public String getReceiverUser()
		{
			return receiverUser;
		}
		public Date getDate()
		{
			return date;
		}
		
		public String getText()
		{
			return text;
		}
		
		public Message build()
		{
			return new Message(flyerId, customerUser, senderUser, receiverUser, date, text);
		}
	}
}
