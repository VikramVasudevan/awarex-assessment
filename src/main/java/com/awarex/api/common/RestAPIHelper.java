package com.awarex.api.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;

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

	public static Pair<Integer, String> invokePostAPI(String label, String uri, Entity payload)
			throws AwarexAPIException {
		System.out.println("Calling POST API " + label + " ; uri = " + uri);
		Client client = ClientBuilder.newClient();
		try {
			WebTarget target = client.target(uri);
			Response response = target.request().header("Authorization", "Bearer " + accessToken).post(payload);
			System.out.println("response.getStatus()  = " + response.getStatus());
			System.out.println("response.getStatusInfo()  = " + response.getStatusInfo());
			int status = response.getStatus();
			if (status == 200 || status == 201 || status == 202)
				return Pair.of(status, response.readEntity(String.class));
			else {
				String errorRes = response.readEntity(String.class);
				System.out.println("errorRes  = " + errorRes);
				List errorList = new Gson().fromJson(errorRes, List.class);
				Map responseMap = new HashMap();
				responseMap.put("developerErrorMessage", "Errors by " + label);
				responseMap.put("errors", errorList);

				return Pair.of(status, new Gson().toJson(responseMap));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map responseMap = new HashMap();
			responseMap.put("developerErrorMessage", "Errors by " + label);
			responseMap.put("errors", Arrays.asList(e.getLocalizedMessage()));

			return Pair.of(500, new Gson().toJson(responseMap));
		} finally {
			client.close();
		}
	}
}
