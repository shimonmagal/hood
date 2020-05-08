package com.hood.server.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		File storageDirectory = new File(".storage");
		
		logger.info("Initialzing local stroage directory {}", storageDirectory.getAbsolutePath());
		
		storageDirectory.mkdirs();
		
		return new BlobInterface(storageDirectory);
	}
	
	private final File storageDirectory;
	
	private BlobInterface(File storageDirectory)
	{
		this.storageDirectory = storageDirectory;
	}
	
	public boolean put(String key, byte[] value)
	{
		return false;
	}
}
