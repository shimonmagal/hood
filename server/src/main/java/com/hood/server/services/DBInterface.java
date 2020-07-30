package com.hood.server.services;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hood.server.model.Message;
import com.hood.server.model.Session;
import com.hood.server.model.User;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.hood.server.model.Flyer;

import com.hood.server.config.HoodConfig;

public class DBInterface
{
	private static final Logger logger = LoggerFactory.getLogger(DBInterface.class);
	
	private static DBInterface instance;
	
	public enum LogicalOperator
	{
		AND("$and"),
		OR("$or");
		
		private String mongoSyntax;
		
		LogicalOperator(String mongoSyntax)
		{
			this.mongoSyntax = mongoSyntax;
		}
		
		public String mongoSyntax()
		{
			return mongoSyntax;
		}
	}
	
	public enum Operator
	{
		EQUALS // todo other operators
	}
	
	public static class OperatorAndValue
	{
		Operator op;
		String val;
		
		private OperatorAndValue(Operator op, String val)
		{
			this.op = op;
			this.val = val;
		}
		
		public static OperatorAndValue of(Operator op, String val)
		{
			return new OperatorAndValue(op, val);
		}
	}
	
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
		String mongoAddress = HoodConfig.get().mongoUrl();
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
			
			MongoCollection<Document> users = getHoodDatabase().getCollection(User.ENTITY_PLURAL_NAME);
			users.createIndex(Indexes.ascending("email"), new IndexOptions().unique(true));
			
			MongoCollection<Document> sessions = getHoodDatabase().getCollection(Session.ENTITY_PLURAL_NAME);
			sessions.createIndex(Indexes.text("session"), new IndexOptions().unique(true));
			
			MongoCollection<Document> messages = getHoodDatabase().getCollection(Message.ENTITY_PLURAL_NAME);
			messages.createIndex(Indexes.compoundIndex(Indexes.text("customerUser"), Indexes.text("flyerId")), new IndexOptions().unique(false));
			messages.createIndex(Indexes.ascending("date"), new IndexOptions().expireAfter(5L, TimeUnit.DAYS));
			
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
		catch (MongoWriteException e)
		{
			if (e.getError().getCode() == 11000) // duplicate
			{
				return true;
			}
			
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
	
	
	public List<Document> query(String collectionName, String field, OperatorAndValue opAndValue)
	{
		Map<String, OperatorAndValue> fieldToOperatorAndValue = Maps.newHashMap();
		fieldToOperatorAndValue.put(field, opAndValue);
		
		return query(collectionName, LogicalOperator.AND, fieldToOperatorAndValue);
	}
	
	public List<Document> query(String collectionName, LogicalOperator logicOp, Map<String, OperatorAndValue> fieldToOperatorAndValue)
	{
		BasicDBList compoundQuery = new BasicDBList();
		
		for (Map.Entry<String, OperatorAndValue> fieldEntry : fieldToOperatorAndValue.entrySet())
		{
			// only equals supported now - todo
			String val = fieldEntry.getValue().val;
			DBObject clause = new BasicDBObject(fieldEntry.getKey(), fieldEntry.getKey().equals("_id") ? new ObjectId(val) : val);
			compoundQuery.add(clause);
		}
		
		BasicDBObject query = new BasicDBObject(logicOp.mongoSyntax(), compoundQuery);
		
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			MongoCursor<Document> cursor = collection.find(query).iterator();
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
			Point position = new Point(new Position(longitude, latitude));
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
			logger.error("Error getting nearest Documents", e);
			return null;
		}
	}
	
	public Document getDocument(String collectionName, Document document)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			return collection.find(document).first();
		}
		catch (Exception e)
		{
			logger.error("Error getting Document {}", document, e);
		}
		
		return null;
	}
	
	public boolean deleteDocument(String collectionName, Document document)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			return collection.deleteOne(document).getDeletedCount() > 0;
		}
		catch (Exception e)
		{
			logger.error("Error deleting Document {}", document, e);
		}
		
		return false;
	}
	
	public List<Document> findRecentTwoOrConditions(String collectionName, LogicalOperator op, String fieldA, List<String> valuesA, String fieldB, List<String> valuesB, String dateField, String ... moreFieldsToRetrieve)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			
			List<Document> matchConditions = Lists.newLinkedList();
			
			for (String value : valuesA)
			{
				matchConditions.add(new Document(fieldA, value));
			}
			
			for (String value : valuesB)
			{
				matchConditions.add(new Document(fieldB, value));
			}
			
			List<Document> result = Lists.newArrayList();
			
			Document group = new Document("_id", new Document(fieldA, "$" + fieldA)
					.append(fieldB, "$" + fieldB))
					.append("max", new Document("$max", "$" + dateField));
			
			for (String field : moreFieldsToRetrieve)
			{
				group = group.append(field, new Document("$last", "$" + field));
			}
			
			AggregateIterable<Document> mongoResult = collection.aggregate(Arrays.asList(
					Aggregates.match(new Document(op.mongoSyntax, matchConditions)),
					new Document("$group", group),
					new Document("$sort", new Document("max", -1))));
			
			for (Document record : mongoResult)
			{
				result.add(record);
			}
			
			return result;
		}
		catch (Exception e)
		{
			logger.info("findRecentTwoOrConditions failed for collection: {}", collectionName, e);
			return null;
		}
	}
	
	public boolean watch(String collectionName, Consumer<Message> action)
	{
		try
		{
			MongoCollection<Document> collection = getHoodDatabase().getCollection(collectionName);
			ChangeStreamIterable<Document> publisher = collection.watch();
			
			publisher.forEach(x -> {
				action.accept(Message.fromBsonObject(x.getFullDocument()));
			});
		}
		catch (MongoCommandException e)
		{
			if (e.getErrorCode() == 40573)
			{
				logger.info("Watch not supported for collection: {}", collectionName);
				return false;
			}
			
			logger.error("Error watching collection {}", collectionName);
			return false;
		}
		
		return true;
	}
}
