package com.hood.server.config;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;

public class HoodConfig
{
	private static final Logger logger = LoggerFactory.getLogger(HoodConfig.class);
	
	private static HoodConfigParameters instance;
	
	public static HoodConfigParameters get()
	{
		if (instance != null)
		{
			return instance;
		}
		
		synchronized (HoodConfigParameters.class)
		{
			if (instance != null)
			{
				return instance;
			}
			
			instance = HoodConfig.create();
			return instance;
		}
	}
	
	private static HoodConfigParameters create()
	{
		try
		{
			ConfigFilesProvider configFilesProvider = new HoodConfigFilesProvider();
			ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);
			
			logger.info("Loading config from: {}", configFilesProvider);
			
			ConfigurationProvider provider = new ConfigurationProviderBuilder()
				.withConfigurationSource(source)
				.withReloadStrategy(new PeriodicalReloadStrategy(5, TimeUnit.SECONDS))
				.build();
			
			return provider.bind("", HoodConfigParameters.class);
		}
		catch (Exception e)
		{
			logger.error("Error loading configuration", e);
			return null;
		}
	}
}
