package com.hood.server;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hood.server.services.DBInterface;
import com.hood.server.services.BlobInterface;

import com.hood.server.config.HoodConfig;

public class HoodServer
{
	private static final int DEFAULT_PORT = 8080;
	
	private static final Logger logger = LoggerFactory.getLogger(HoodServer.class);
	
	private static boolean initializeServices()
	{
		if (HoodConfig.get() == null)
		{
			return false;
		}
		
		if (!DBInterface.get().initialize())
		{
			return false;
		}
		
		if (!BlobInterface.get().initialize())
		{
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) throws Exception
	{
		if (!initializeServices())
		{
			return;
		}
		
		
		HttpServer server = HttpServer.createSimpleServer("/", HoodConfig.get().serverPort());
		
		// api
		HttpHandler apiHandler = new GrizzlyHttpContainerProvider()
				.createContainer(HttpHandler.class, JaxRsApiResourceConfig.create());
		server.getServerConfiguration().addHttpHandler(apiHandler, "/api");
		
		// message websocket
		WebSocketAddOn addOn = new WebSocketAddOn();
		addOn.setTimeoutInSeconds(60);
		for (NetworkListener listener : server.getListeners()) {
			listener.registerAddOn(addOn);
		}
		
		WebSocketEngine.getEngine().register("", "/messages", new MessageHandler());
		
		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				server.shutdownNow();
			}
		}));
		
		server.start();
	}
}
