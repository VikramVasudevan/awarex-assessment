package com.awarex.api.userposts;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;

import com.awarex.api.common.AwarexAPIException;
import com.awarex.api.common.AwarexEnvironment;
import com.awarex.api.common.RestAPIHelper;
import com.awarex.api.model.CombinedUserPostModel;
import com.awarex.api.model.User;
import com.awarex.api.model.UserPost;
import com.awarex.api.model.UserPostPayload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/userposts")
public class UserPostManagerService {
	Type userType = new TypeToken<User>() {
	}.getType();

	Type usersType = new TypeToken<List<User>>() {
	}.getType();
	Type userPostsType = new TypeToken<List<UserPost>>() {
	}.getType();

	Gson gson = new Gson();

	String usersAPIUrl = AwarexEnvironment.getInstance().backendAPIBaseURI + "users";
	String userPostsAPIUrl = AwarexEnvironment.getInstance().backendAPIBaseURI + "posts";

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("create")
	public Response writeAPost(UserPostPayload payload) {
		String currentlyCallingAPI = "";
		try {
			List<User> userInfo = gson.fromJson(
					RestAPIHelper.invokeGetAPI("Fetch Users API", usersAPIUrl + "?email=" + payload.email), usersType);
			System.out.println("userInfo = " + gson.toJson(userInfo));

			int userId = 0;

			if (userInfo != null && userInfo.size() > 0) {
				// user exists. add the post.
				userId = userInfo.get(0).getUserId();
			} else {
				// create user
				currentlyCallingAPI = "/users";
				Pair<Integer, String> userCreateResponse = RestAPIHelper.invokePostAPI("Create User API", usersAPIUrl,
						Entity.json(payload.toUserPayload()));
				System.out.println(userCreateResponse);
				if (userCreateResponse.getLeft() > 299) {
					return Response.status(userCreateResponse.getLeft()).entity(userCreateResponse.getRight()).build();
				}
				User user = gson.fromJson(userCreateResponse.getRight(), userType);

				System.out.println(gson.toJson(user));

				userId = user.id;
			}

			if (userId > 0) {
				currentlyCallingAPI = "/posts";
				Pair<Integer, String> response = RestAPIHelper.invokePostAPI("Create User Posts API",
						usersAPIUrl + "/" + userId + "/posts", Entity.json(payload));
				System.out.println(response);

				return Response.status(response.getLeft()).entity(response.getRight()).build();

			}

			throw new AwarexAPIException("Unable to create user");
		} catch (Exception e) {
			e.printStackTrace();
			Map responseMap = new HashMap();
			responseMap.put("developerErrorMessage", "Errors by " + currentlyCallingAPI + " API");
			responseMap.put("errors", Arrays.asList(e.getLocalizedMessage()));
			return Response.status(500).entity(gson.toJson(responseMap)).build();
		}

	}

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("get")
//	public String getAllPosts() {
//		List responseRows = new ArrayList();
//		try {
//			responseRows = gson.fromJson(RestAPIHelper.invokeGetAPI("Fetch User Posts API", userPostsAPIUrl),
//					List.class);
//			return gson.toJson(responseRows);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new WebApplicationException(e.getLocalizedMessage(), 500);
//		}
//	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUserPostDetails")
	public String getUserPostDetails() {
		try {
			ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);

			Future<List<User>> usersFuture = executor.submit(() -> {
				// TODO do multiple iterations to get all users? pagination?
				List<User> users;
				users = gson.fromJson(RestAPIHelper.invokeGetAPI("Fetch Users API", usersAPIUrl), usersType);
				return users;
			});

			Future<List<UserPost>> userPostsFuture = executor.submit(() -> {
				// TODO do multiple iterations to get all posts? pagination?
				List<UserPost> userPosts = gson
						.fromJson(RestAPIHelper.invokeGetAPI("Fetch User Posts API", userPostsAPIUrl), userPostsType);
				return userPosts;
			});

			List<User> users = usersFuture.get();
			List<UserPost> userPosts = userPostsFuture.get();

			Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, user -> user));

			// Organize posts by user
			Map<Integer, List<CombinedUserPostModel>> userPostsPerUser = userPosts.stream().map(userPost -> {
				User user = userMap.get(userPost.user_id);
				if (user != null) {
					return new CombinedUserPostModel(userPost.id, userPost.user_id, userPost.title, userPost.body,
							user.name, user.email, user.gender, user.status);
				} else {
					return new CombinedUserPostModel(userPost.id, userPost.user_id, userPost.title, userPost.body, null,
							null, null, null);
				}
			}).filter(java.util.Objects::nonNull).collect(Collectors.groupingBy(CombinedUserPostModel::getUserId,
					Collectors.collectingAndThen(Collectors.toList(), list -> {
						list.sort(Comparator.comparingInt(CombinedUserPostModel::getPostId));
						return list;
					})));

			// Calculate users without posts
			int usersWithoutPosts = users.stream().filter(user -> {
				return userPosts.stream().noneMatch(userPost -> userPost.user_id == user.id);
			}).collect(Collectors.toList()).size();

			// Calculate users with posts
			int usersWithPosts = users.size() - usersWithoutPosts;

			// Calculate posts without users
			int postsWithoutUsers = userPosts.stream().filter(userPost -> {
				return !userMap.containsKey(userPost.user_id);
			}).collect(Collectors.toList()).size();

			Map responseMap = new HashMap();
			responseMap.put("users", userMap);
			responseMap.put("userPosts", userPostsPerUser);
			responseMap.put("usersWithoutPosts", usersWithoutPosts);
			responseMap.put("usersWithPosts", usersWithPosts);
			responseMap.put("postsWithoutUsers", postsWithoutUsers);
			responseMap.put("totalUsers", users.size());
			responseMap.put("totalUserPosts", userPosts.size());
			return gson.toJson(responseMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}

	}

}
