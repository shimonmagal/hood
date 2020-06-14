package com.hood.server;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.RuntimeDelegate;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.hood.server.api.JaxRsApiApplication;
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
		
		HttpServer server = HttpServer.create(new InetSocketAddress(HoodConfig.get().serverPort()), 0);
		
		logger.info("Listening on port: {}", HoodConfig.get().serverPort());
		
		HttpHandler apiHandler = RuntimeDelegate.getInstance().createEndpoint(new JaxRsApiApplication(), HttpHandler.class);
		server.createContext("/api", apiHandler);
		
		ExecutorService clientsThreadPool = Executors.newFixedThreadPool(100);
		server.setExecutor(Executors.newFixedThreadPool(100));

		server.start();
	}
}
