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
		Response response = target.request().header("Authorization", "Bearer " + accessToken).get();
		return response.readEntity(String.class);
	}

	public static String invokePostAPI(String uri, Entity payload) throws AwarexAPIException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(uri);
		Response response = target.request().header("Authorization", "Bearer " + accessToken).post(payload);
		if (response.getStatus() == 200)
			return response.readEntity(String.class);
		else
			throw new AwarexAPIException(response.readEntity(String.class));
	}
}
