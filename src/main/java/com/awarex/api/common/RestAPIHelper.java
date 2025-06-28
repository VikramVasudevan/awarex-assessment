package com.awarex.api.common;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class RestAPIHelper {
	public RestAPIHelper() {
		// TODO Auto-generated constructor stub
	}

	public static String invokeGetAPI(String uri) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(uri);
		Response response = target.request().get();
		return response.readEntity(String.class);
	}

}
