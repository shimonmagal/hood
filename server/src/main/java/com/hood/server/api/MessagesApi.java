package com.hood.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hood.server.model.Conversation;
import com.hood.server.model.Flyer;
import com.hood.server.model.Message;
import com.hood.server.model.User;
import com.hood.server.services.DBInterface;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.hood.server.services.DBInterface.LogicalOperator;
import static com.hood.server.services.DBInterface.Operator;
import static com.hood.server.services.DBInterface.OperatorAndValue;

public class MessagesApi
{
	private static final Logger logger = LoggerFactory.getLogger(MessagesApi.class);
	
	public static boolean pushMessage(String messageStr, String senderUser, Consumer<Message> consumer)
	{
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper();
			Message.Builder messageBuilder = jsonMapper.readValue(messageStr, Message.Builder.class);
			messageBuilder.withSenderUser(senderUser);
			Message message = messageBuilder.build();
			
			if (consumer != null)
			{
				consumer.accept(message);
			}
			
			Document messageDoc = message.toBsonObject();
			return DBInterface.get().addDocument(Message.ENTITY_PLURAL_NAME, messageDoc);
		}
		catch (Exception e)
		{
			logger.error("Failed to pushMessage: {}", messageStr, e);
			return false;
		}
	}
	
	public static String getAllMessageInConversation(String authenticactedUser, String initiatingUser, String flyerId)
	{
		if (!initiatingUser.equals(authenticactedUser))
		{
			if (!FlyersApi.validateFlyerIsByUser(flyerId, authenticactedUser))
			{
				return null;
			}
		}
		
		try
		{
			Map<String, OperatorAndValue> fieldToOperatorAndValue = Maps.newHashMap();
			
			fieldToOperatorAndValue.put(Message.CUSTOMER_USER_FIELD, OperatorAndValue.of(Operator.EQUALS, initiatingUser));
			fieldToOperatorAndValue.put(Message.FLYER_ID_FIELD, OperatorAndValue.of(Operator.EQUALS, flyerId));
			
			List<Document> documents = DBInterface.get().query(Message.ENTITY_PLURAL_NAME, LogicalOperator.AND, fieldToOperatorAndValue);
			
			if (documents == null)
			{
				return null;
			}
			
			List<Message> messages = documents.stream()
					.map(doc -> Message.fromBsonObject(doc))
					.filter(message -> message != null)
					.collect(Collectors.toList());
			
			ObjectMapper jsonMapper = new ObjectMapper();
			String resultJson = jsonMapper.writeValueAsString(messages);
			
			return resultJson;
		}
		catch (Exception e)
		{
			logger.error("Error getting message", e);
			return null;
		}
	}
	
	public static boolean wathcForUpdates(Consumer<Message> consumer)
	{
		if (DBInterface.get().watch(Message.ENTITY_PLURAL_NAME, consumer))
		{
			return true;
		}
		
		logger.warn("watch for messages is not supported");
		
		return false;
	}
	
	public static String getAllConversations(String authenticactedUser) throws Exception
	{
		List<Document> flyerDocs = DBInterface.get().query(Flyer.ENTITY_PLURAL_NAME, Flyer.USER_FIELD_NAME,
				OperatorAndValue.of(Operator.EQUALS, authenticactedUser));
		List<Flyer> myFlyers = flyerDocs.stream().map(flyer -> Flyer.fromBsonObject(flyer)).collect(Collectors.toList());
		List<String> myFlyerIds = myFlyers.stream().map(flyer -> flyer.getId()).collect(Collectors.toList());
		
		List<Document> conversationLatestMessages = DBInterface.get().findRecentTwoOrConditions(Message.ENTITY_PLURAL_NAME,
				LogicalOperator.OR, Message.FLYER_ID_FIELD, myFlyerIds, Message.CUSTOMER_USER_FIELD,
				Arrays.asList(authenticactedUser), Message.DATE_FIELD,
				Message.TEXT_FIELD);
		
		List<Conversation> conversations = toConversations(conversationLatestMessages, myFlyerIds);
		
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(conversations);
	}
	
	private static List<Conversation> toConversations(List<Document> conversationDocs, List<String> myFlyerIds)
	{
		Set<String> myFlyersIdsSet = Sets.newHashSet();
		myFlyersIdsSet.addAll(myFlyerIds);
		
		List<Conversation> conversations = Lists.newArrayList();
		
		for (Document conversationDoc : conversationDocs)
		{
			String photoUrl = null;
			String conversationFlyerId = ((Document) conversationDoc.get("_id")).getString(Message.FLYER_ID_FIELD);
			String conversationCustomerUsername = ((Document) conversationDoc.get("_id")).getString(Message.CUSTOMER_USER_FIELD);
			Date date = conversationDoc.getDate("max");
			String text = conversationDoc.getString(Message.TEXT_FIELD);
			String title = null;
			
			if (!myFlyersIdsSet.contains(conversationFlyerId))
			{
				List<Document> flyerDocs = DBInterface.get().query(Flyer.ENTITY_PLURAL_NAME, Flyer.FLYER_ID_FIELD,
						OperatorAndValue.of(Operator.EQUALS, conversationFlyerId));
				
				if ((flyerDocs == null) ||
						(flyerDocs.size() != 1))
				{
					logger.warn("toConversations failed for conversationFlyerId: {}", conversationFlyerId);
					continue;
				}
				
				photoUrl = flyerDocs.get(0).getString(Flyer.IMAGE_KEY_FIELD);
				title = flyerDocs.get(0).getString(Flyer.TITLE_FIELD);
			}
			else
			{
				
				List<Document> userDocs = DBInterface.get().query(User.ENTITY_PLURAL_NAME, User.USERNAME_FIELD,
						OperatorAndValue.of(Operator.EQUALS, conversationCustomerUsername));
				
				if ((userDocs == null) ||
						(userDocs.size() != 1))
				{
					logger.warn("toConversations failed for conversationCustomerUsername: {}", conversationCustomerUsername);
					continue;
				}
				
				photoUrl = userDocs.get(0).getString(User.PICTURE_FIELD);
				title = null;
			}
			
			conversations.add(new Conversation(conversationFlyerId, conversationCustomerUsername, date, text, photoUrl, title));
		}
		
		return conversations;
	}
}
