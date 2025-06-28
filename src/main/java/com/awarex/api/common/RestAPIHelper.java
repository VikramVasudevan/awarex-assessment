package com.awarex.api.common;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class RestAPIHelper {
	private final static String accessToken = AwarexEnvironment.getInstance().accessToken;

	public static String invokeGetAPI(String uri) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(uri);
		Response response = target.request().get();
		return response.readEntity(String.class);
	}

	public static String invokePostAPI(String uri, Entity payload) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(uri);
		Response response = target.request().header("Authorization", "Bearer " + accessToken).post(payload);
		return response.readEntity(String.class);
	}
}
