package com.hood.server.services;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.HoodConfig;

public class BlobInterface
{
	private static final Logger logger = LoggerFactory.getLogger(BlobInterface.class);
	
	private static BlobInterface instance;
	
	public static BlobInterface get()
	{
		if (instance != null)
		{
			return instance;
		}
		
		synchronized (BlobInterface.class)
		{
			if (instance != null)
			{
				return instance;
			}
			
			instance = BlobInterface.createLocal();
			return instance;
		}
	}
	
	private static BlobInterface createLocal()
	{
		File storageDirectory = new File(HoodConfig.get().localStorageDirectory());
		
		logger.info("Initialzing local stroage directory {}", storageDirectory.getAbsolutePath());
		
		storageDirectory.mkdirs();
		
		return new BlobInterface(storageDirectory);
	}
	
	private final File storageDirectory;
	
	private BlobInterface(File storageDirectory)
	{
		this.storageDirectory = storageDirectory;
	}
	
	public boolean initialize()
	{
		return true;
	}
	
	public String putNew(InputStream inputStream)
	{
		String newObjectKey = UUID.randomUUID().toString();
		File targetFile = new File(storageDirectory, newObjectKey);
		
		try
		{
			Files.copy(
				inputStream,
				targetFile.toPath());
			
			return newObjectKey;
		}
		catch (Exception e)
		{
			logger.error("Error putting blob file: {}", targetFile, e);
			return null;
		}
	}
	
	public File getFile(String key)
	{
		return new File(storageDirectory, key);
	}
}
