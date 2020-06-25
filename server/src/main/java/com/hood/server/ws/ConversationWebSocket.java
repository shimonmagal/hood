package com.hood.server.ws;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hood.server.api.MessagesApi;
import com.hood.server.session.SessionManager;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationWebSocket extends SecuredWebSocket
{
	private static final Logger logger = LoggerFactory.getLogger(ConversationWebSocket.class);
	
	private Cache<WebSocket, String> webSocketToUser = CacheBuilder.newBuilder().build();
	private Cache<String, WebSocket> userToWebsocket = CacheBuilder.newBuilder().build();
	
	@Override
	protected WebSocket createSecuredSocket(WebSocket webSocket, String authenticatedUser)
	{
		webSocketToUser.put(webSocket, authenticatedUser);
		userToWebsocket.put(authenticatedUser, webSocket);
		
		return webSocket;
	}
	
	@Override
	public void onClose(WebSocket socket, DataFrame frame)
	{
		logger.debug("ConversationWebSocket - disconnected with user: {}", webSocketToUser.getIfPresent(socket));
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
		logger.debug("ConversationWebSocket - connected with user: {}", webSocketToUser.getIfPresent(socket));
		
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
