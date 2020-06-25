package com.hood.server.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hood.server.api.MessagesApi;
import com.hood.server.model.Message;
import com.hood.server.session.SessionManager;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MessagesWebSocket extends SecuredWebSocket
{
	private static final Logger logger = LoggerFactory.getLogger(MessagesWebSocket.class);
	
	private Cache<WebSocket, String> webSocketToUser = CacheBuilder.newBuilder().build();
	private Cache<String, WebSocket> conversationIdToWebsocket = CacheBuilder.newBuilder().build();
	
	private Consumer<Message> messageConsumer = message -> {
		String flyerId = message.getFlyerId();
		String customerUser = message.getCustomerUser();
		String receiverUser = message.getReceiverUser();
		
		String conversationId = String.join("#", flyerId, customerUser, receiverUser);
		
		WebSocket socket = conversationIdToWebsocket.getIfPresent(conversationId);
		
		if (socket == null)
		{
			logger.debug("socket is null");
			return;
		}
		
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper();
			String resultJson = jsonMapper.writeValueAsString(message);
			
			socket.send("[" + resultJson + "]");
		}
		catch (Exception e)
		{
			logger.error("Error while sending message", e);
		}
	};
	
	final boolean isUsingWatch;
	
	public MessagesWebSocket()
	{
		isUsingWatch = MessagesApi.wathcForUpdates(messageConsumer);
	}
	
	@Override
	protected WebSocket createSecuredSocket(WebSocket webSocket, String authenticatedUser)
	{
		webSocketToUser.put(webSocket, authenticatedUser);
		
		return webSocket;
	}
	
	@Override
	public void onClose(WebSocket socket, DataFrame frame)
	{
		logger.debug("messagesWebSocket - disconnected with user: {}", webSocketToUser.getIfPresent(socket));
		
		super.onClose(socket, frame);
		
		webSocketToUser.invalidate(socket);
		List<String> foundKeys = conversationIdToWebsocket.asMap().entrySet().stream().filter(entry -> entry.getValue() == socket).
				map(entry -> entry.getKey()).collect(Collectors.toList());
		
		for (String key : foundKeys)
		{
			conversationIdToWebsocket.invalidate(key);
		}
		
	}
	
	@Override
	public void onConnect(WebSocket socket)
	{
		logger.debug("messagesWebSocket - connected with user: {}", webSocketToUser.getIfPresent(socket));
	}
	
	@Override
	public void onMessage(WebSocket socket, String message)
	{
		Object currentUser = webSocketToUser.getIfPresent(socket);
		
		if (currentUser == null)
		{
			logger.error("Unexpected state: user is null onMessage");
			return;
		}
		
		String currentUsername = currentUser.toString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode jsonObj = null;
		
		try
		{
			jsonObj = mapper.readTree(message);
		}
		catch (JsonProcessingException e)
		{
			logger.error("Error while parsing json: {}", e);
		}
		
		if (jsonObj == null)
		{
			return;
		}
		
		if (jsonObj.has("text"))
		{
			MessagesApi.pushMessage(message, currentUsername, isUsingWatch ? null : messageConsumer);
		}
		else
		{
			String customerUser = jsonObj.get(Message.CUSTOMER_USER_FIELD).asText();
			String flyerId = jsonObj.get(Message.FLYER_ID_FIELD).asText();
			
			String conversationId = String.join("#", flyerId, customerUser, currentUsername);
			
			conversationIdToWebsocket.put(conversationId, socket);
			
			String allMessageInConversation = MessagesApi.getAllMessageInConversation(currentUsername, customerUser, flyerId);
			
			if (allMessageInConversation != null)
			{
				socket.send(allMessageInConversation);
			}
		}
	}
}
