package com.hood.server;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JaxRsApiResourceConfig
{
	public static ResourceConfig create()
	{
		return new ResourceConfig()
				.packages("com.hood.server.api")
				.register(MultiPartFeature.class);
	}
}
