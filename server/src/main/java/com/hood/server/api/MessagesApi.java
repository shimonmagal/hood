package com.hood.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hood.server.model.Message;
import com.hood.server.services.DBInterface;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
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
			
			fieldToOperatorAndValue.put("cusomterUser", OperatorAndValue.of(Operator.EQUALS, initiatingUser));
			fieldToOperatorAndValue.put("flyerId", OperatorAndValue.of(Operator.EQUALS, flyerId));
			
			List<Document> documents = DBInterface.get().query(Message.ENTITY_PLURAL_NAME, LogicalOperator.OR, fieldToOperatorAndValue);
			
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
}
