package com.awarex.api.common;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class RestAPIHelper {
	private final static String accessToken = AwarexEnvironment.getInstance().accessToken;

	public static String invokeGetAPI(String label, String uri) throws AwarexAPIException {
		Client client = ClientBuilder.newClient();
		try {
			System.out.println("Calling GET API " + label + " ; uri = " + uri);

			WebTarget target = client.target(uri);
			Response response = target.request().header("Authorization", "Bearer " + accessToken).get();
			System.out.println("response.getStatus()  = " + response.getStatus());
			System.out.println("response.getStatusInfo()  = " + response.getStatusInfo());
			int status = response.getStatus();
			if (status == 200 || status == 201 || status == 202)
				return response.readEntity(String.class);
			else
				throw new AwarexAPIException("Error invoking " + label + ": " + response.readEntity(String.class));
		} catch (Exception e) {
			throw new AwarexAPIException(label, e);
		} finally {
			client.close();
		}

	}

	public static String invokePostAPI(String label, String uri, Entity payload) throws AwarexAPIException {
		System.out.println("Calling GET API " + label + " ; uri = " + uri);
		Client client = ClientBuilder.newClient();
		try {
			WebTarget target = client.target(uri);
			Response response = target.request().header("Authorization", "Bearer " + accessToken).post(payload);
			System.out.println("response.getStatus()  = " + response.getStatus());
			System.out.println("response.getStatusInfo()  = " + response.getStatusInfo());
			int status = response.getStatus();
			if (status == 200 || status == 201 || status == 202)
				return response.readEntity(String.class);
			else
				throw new AwarexAPIException("Error invoking " + label + ": " + response.readEntity(String.class));
		} catch (Exception e) {
			throw new AwarexAPIException(label, e);
		} finally {
			client.close();
		}
	}
}
