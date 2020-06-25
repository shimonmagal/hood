package com.hood.server.model;

import java.util.Date;

public class Conversation
{
	private final String flyerId;
	private final String customerUser;
	private final String photoUrl;
	private final Date date;
	private final String text;
	private final String title;
	
	public Conversation(String flyerId, String customerUser, Date date, String text, String photoUrl, String title)
	{
		this.flyerId = flyerId;
		this.customerUser = customerUser;
		this.date = date;
		this.text = text;
		this.photoUrl = photoUrl;
		this.title = title;
	}
	
	@Override
	public String toString()
	{
		return String.format("(flyerId: %s) (customerUser: %s) (photoUrl: %s) (date: %s) (text: %s) (title: %s)",
				flyerId, customerUser, photoUrl, date, text, title);
	}
	
	public String getFlyerId()
	{
		return flyerId;
	}
	
	public String getCustomerUser()
	{
		return customerUser;
	}
	
	public String getPhotoUrl()
	{
		return photoUrl;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getTitle()
	{
		return title;
	}
}
