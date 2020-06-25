package com.hood.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hood.server.api.MessagesApi;
import com.hood.server.model.Message;
import com.hood.server.session.SessionManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConversationHandler extends WebSocketApplication
{
	private static final Logger logger = LoggerFactory.getLogger(ConversationHandler.class);
	
	private Cache<WebSocket, String> webSocketToUser = CacheBuilder.newBuilder().build();
	private Cache<String, WebSocket> userToWebsocket = CacheBuilder.newBuilder().build();
	
	@Override
	public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners)
	{
		WebSocket webSocket = super.createSocket(handler, requestPacket, listeners);
		
		String session = requestPacket.getHeader("session");
		
		if (session == null)
		{
			logger.error("createSocket called with no session");
			webSocket.close();
			
			return webSocket;
		}
		
		String user = SessionManager.get(session);
		
		if (user == null)
		{
			logger.error("no user for session: {}", session);
			webSocket.close();
			
			return webSocket;
		}
		
		webSocketToUser.put(webSocket, user);
		userToWebsocket.put(user, webSocket);
		
		return webSocket;
	}
	
	@Override
	public void onClose(WebSocket socket, DataFrame frame)
	{
		System.out.println("disconneted!");
		super.onClose(socket, frame);
		
		String user = webSocketToUser.getIfPresent(socket);
		webSocketToUser.invalidate(socket);
		
		if (user != null)
		{
			webSocketToUser.invalidate(user);
		}
	}
	
	@Override
	public void onConnect(WebSocket socket)
	{
		System.out.println("Connected!");
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
		
		try
		{
			String convs = MessagesApi.getAllConversations(currentUsername);
			socket.send(convs);
		}
		catch (Exception e)
		{
			logger.error("Error in getAllConversations", e);
			socket.send("[]");
		}
	}
}
