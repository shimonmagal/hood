package com.hood.server.ws;

import com.hood.server.session.SessionManager;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecuredWebSocket extends WebSocketApplication
{
	private static final Logger logger = LoggerFactory.getLogger(SecuredWebSocket.class);
	
	@Override
	public final WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners)
	{
		WebSocket webSocket = super.createSocket(handler, requestPacket, listeners);
		
		String session = requestPacket.getHeader("session");
		
		if (session == null)
		{
			logger.error("createSocket called with no session");
			webSocket.close();
			
			return webSocket;
		}
		
		String authenticatedUser = SessionManager.get(session);
		
		if (authenticatedUser == null)
		{
			logger.error("no user for session: {}", session);
			webSocket.close();
			
			return webSocket;
		}
		
		return createSecuredSocket(webSocket, authenticatedUser);
	}
	
	protected abstract WebSocket createSecuredSocket(WebSocket webSocket, String authenticatedUser);
}