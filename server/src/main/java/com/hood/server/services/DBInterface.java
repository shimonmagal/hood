package com.hood.server.services;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.hood.server.model.Flyer;

public class DBInterface
{
	private static final Logger logger = LoggerFactory.getLogger(DBInterface.class);
	
	private static DBInterface instance;
	
	public static DBInterface get()
	{
		if (instance != null)
		{
			return instance;
		}
		
		synchronized (DBInterface.class)
		{
			if (instance != null)
			{
				return instance;
			}
			
			instance = DBInterface.createLocal();
			return instance;
		}
	}
	
	private static DBInterface createLocal()
	{
		String mongoAddress = "localhost:27017";
		MongoClient mongoClient = null;
		
		try
		{
			logger.info("Initializing local mongo client: {}", mongoAddress);
			
			mongoClient = MongoClients.create("mongodb://" + mongoAddress);
		}
		catch (Exception e)
		{
			logger.error("Error initializing Local Mongo DB, make sure it runs on: {}", mongoAddress);
			return null;
		}
		
		DBInterface dbInterface = new DBInterface(mongoClient);
		
		return dbInterface;
	}
	
	private final MongoClient mongoClient;
	
	private DBInterface(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}
	
	private MongoDatabase getHoodDatabase()
	{
		return mongoClient.getDatabase("hood");
	}
	
	public boolean initialize()
	{
		if (!checkConnectivity())
		{
			return false;
		}
		
		if (!buildIndexes())
		{
			return false;
		}
		
		return true;
	}
	
	public boolean checkConnectivity()
	{
		try
		{
			logger.info("Checking DB connectivity");
			
			Document serverStatus = getHoodDatabase().runCommand(new Document("serverStatus", 1));
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error getting server status from Mongo", e);
			return false;
		}
	}
	
	private boolean buildIndexes()
	{
		try
		{
			logger.info("Building DB Indexes");
			
			MongoCollection<Document> flyers = getHoodDatabase().getCollection(Flyer.ENTITY_PLURAL_NAME);
			
			flyers.createIndex(Indexes.geo2dsphere("location"));
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error building indexes", e);
			return false;
		}
	}
	
	public boolean addDocument(String collectionName, Document bsonObject)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			
			collection.insertOne(bsonObject);
			
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error saving Document", e);
			return false;
		}
	}
	
	public List<Document> getAllDocuments(String collectionName)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			MongoCursor<Document> cursor = collection.find().iterator();
			List<Document> result = new ArrayList();
			
			try
			{
				while (cursor.hasNext())
				{
					result.add(cursor.next());
				}
			}
			finally
			{
				cursor.close();
			}
			
			return result;
		}
		catch (Exception e)
		{
			logger.error("Error getting Documents", e);
			return null;
		}
	}
	
	public List<Document> getNearestDocuments(String collectionName, String locationFieldName,
		double longitude, double latitude, double maxDistanceInMetters, double minDistanceInMetters)
	{
		try
		{
			Point position = new Point(new Position(latitude, longitude));
			Bson nearFilter = Filters.near(locationFieldName, position, maxDistanceInMetters, minDistanceInMetters);
			
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			MongoCursor<Document> cursor = collection.find(nearFilter).iterator();
			List<Document> result = new ArrayList();
			
			try
			{
				while (cursor.hasNext())
				{
					result.add(cursor.next());
				}
			}
			finally
			{
				cursor.close();
			}
			
			return result;
		}
		catch (Exception e)
		{
			logger.error("Error getting Documents", e);
			return null;
		}
	}
}