package com.hood.server.session;

import com.hood.server.api.auth.AuthenticationFilter;
import com.hood.server.model.Session;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;

public class SessionManager
{
    private static String SESSION_COLLECTION_NAME = "sessions";
    
    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    public static String createSession(String email)
    {
        String sessionId = Long.toString(new Random().nextLong());
    
        if (sessionId == null)
        {
            logger.error("createSession: session is null for email: {}", email);
            return null;
        }
    
        Session session = new Session(sessionId, email, new Date());
        
        if (!DBInterface.get().addDocument(SESSION_COLLECTION_NAME, session.toBsonObject()))
        {
            logger.error("Failed adding document with session for email: {}", email);
            return null;
        }
        
        return sessionId;
    }

    public static String get(String sessionId)
    {
        Session session = new Session(sessionId);

        Document result = DBInterface.get().getDocument(SESSION_COLLECTION_NAME, session.toBsonObject());
        
        if (result == null)
        {
            return null;
        }
    
        Session sessionResult = Session.fromBsonDocument(result);
        
        return sessionResult.getEmail();
    }
}
