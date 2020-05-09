package com.hood.server.api;

import com.hood.server.api.auth.AuthenticationFilter;
import com.hood.server.api.auth.FacebookLoginApi;
import com.hood.server.api.auth.GoogleLoginApi;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.*;

public class JaxRsApiApplication extends Application
{
	private final Set<Class<?>> classes;
	
	public JaxRsApiApplication()
	{
		Set<Class<?>> c = new HashSet<Class<?>>();
		
		c.add(MultiPartFeature.class);
		c.add(AuthenticationFilter.class);
		c.add(GoogleLoginApi.class);
		c.add(BlobApi.class);
		c.add(FlyersApi.class);
		c.add(FacebookLoginApi.class);
		
		classes = Collections.unmodifiableSet(c);
	}
	
	@Override
	public Set<Class<?>> getClasses()
	{
		return classes;
	}
	
	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> props = new HashMap<>();
		props.put("jersey.config.server.provider.classnames",
				"org.glassfish.jersey.media.multipart.MultiPartFeature");
		return props;
	}
}