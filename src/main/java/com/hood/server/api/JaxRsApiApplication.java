package com.hood.server.api;

import com.hood.server.api.auth.AuthenticationFilter;
import com.hood.server.api.report.Report;
import com.hood.server.api.auth.LoginApi;
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
		c.add(LoginApi.class);
		c.add(FileApi.class);
		
		c.add(Report.class);
		
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