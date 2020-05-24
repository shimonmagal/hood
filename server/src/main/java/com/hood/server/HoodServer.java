package com.hood.server;

import com.hood.server.api.JaxRsApiApplication;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

import com.hood.server.services.DBInterface;
import com.hood.server.services.BlobInterface;

public class HoodServer
{
	private static final int DEFAULT_PORT = 8080;
	
	private static final Logger logger = LoggerFactory.getLogger(HoodServer.class);
	
	private static boolean initializeServices()
	{
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
		
		int port = DEFAULT_PORT;
		
		if (args.length > 0)
		{
			port = Integer.parseInt(args[0]);
		}
		
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
		logger.info("Listening on port: {}", port);
		
		HttpHandler apiHandler = RuntimeDelegate.getInstance().createEndpoint(new JaxRsApiApplication(), HttpHandler.class);
		server.createContext("/api", apiHandler);
		
		server.setExecutor(null);
		server.start();
	}
}
