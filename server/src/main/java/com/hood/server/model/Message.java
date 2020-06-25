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
	public static final String CUSTOMER_USER_FIELD = "customerUser";
	public static final String SENDER_USER_FIELD = "senderUser";
	public static final String RECEIVER_USER_FIELD = "receiverUser";
	public static final String FLYER_ID_FIELD = "flyerId";
	public static final String DATE_FIELD = "date";
	public static final String TEXT_FIELD = "text";
	
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
				.withFlyerId(bson.getString(FLYER_ID_FIELD))
				.withCustomerUser(bson.getString(CUSTOMER_USER_FIELD))
				.withSenderUser(bson.getString(SENDER_USER_FIELD))
				.withReceiverUser(bson.getString(RECEIVER_USER_FIELD))
				.withDate(bson.getDate(DATE_FIELD))
				.withText(bson.getString(TEXT_FIELD))
				.build();
	}
	
	public Document toBsonObject()
	{
		Document bson = new Document();
		
		if (flyerId != null)
		{
			bson.append(FLYER_ID_FIELD, flyerId);
		}
		
		if (customerUser != null)
		{
			bson.append(CUSTOMER_USER_FIELD, customerUser);
		}
		
		if (senderUser != null)
		{
			bson.append(SENDER_USER_FIELD, senderUser);
		}
		
		if (receiverUser != null)
		{
			bson.append(RECEIVER_USER_FIELD, receiverUser);
		}
		
		if (date != null)
		{
			bson.append(DATE_FIELD, date);
		}
		
		if (text != null)
		{
			bson.append(TEXT_FIELD, text);
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
