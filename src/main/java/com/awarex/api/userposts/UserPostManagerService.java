package com.awarex.api.userposts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.awarex.api.common.RestAPIHelper;
import com.google.gson.Gson;

@Path("/userposts")
public class UserPostManagerService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("create")
	public String writeAPost() {
		Map responseMap = new HashMap();
		responseMap.put("message", "Post created");

		return new Gson().toJson(responseMap);

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("find")
	public String findPost() {
		Map responseMap = new HashMap();
		responseMap.put("message", "Here is your Post");

		return new Gson().toJson(responseMap);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get")
	public String getAllPosts() {
		List responseMap = new Gson().fromJson(RestAPIHelper.invokeGetAPI("https://gorest.co.in/public/v2/posts"),
				List.class);
		return new Gson().toJson(responseMap);
	}
}
