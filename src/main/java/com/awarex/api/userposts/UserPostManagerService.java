package com.awarex.api.userposts;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

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
	public String writeAPost(UserPostPayload payload) {
		try {
			List<User> userInfo = gson.fromJson(RestAPIHelper.invokeGetAPI(usersAPIUrl + "?email=" + payload.email),
					usersType);

			int userId = 0;

			if (userInfo != null && userInfo.size() > 0) {
				// user exists. add the post.
				userId = userInfo.get(0).getUserId();
			} else {
				// create user
				String userCreateResponse = RestAPIHelper.invokePostAPI(usersAPIUrl,
						Entity.json(payload.toUserPayload()));
				System.out.println(userCreateResponse);
				List<User> users = gson.fromJson(userCreateResponse, usersType);

				System.out.println(gson.toJson(users));

				userId = users.get(0).id;
			}

			if (userId > 0) {
				String response = RestAPIHelper.invokePostAPI(usersAPIUrl + "/" + userId + "/posts",
						Entity.json(payload));
				return response;
			}

			Map responseMap = new HashMap();
			responseMap.put("message", "user does not exist");

			return gson.toJson(responseMap);
		} catch (Exception e) {
			e.printStackTrace();
			Map responseMap = new HashMap();
			responseMap.put("developerErrorMessage", "Error calling API");
			responseMap.put("errors", e.getLocalizedMessage());
			return gson.toJson(responseMap);

		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("find")
	public String findPost() {
		Map responseMap = new HashMap();
		responseMap.put("message", "Here is your Post");

		return gson.toJson(responseMap);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get")
	public String getAllPosts() {
		List responseRows = gson.fromJson(RestAPIHelper.invokeGetAPI(userPostsAPIUrl), List.class);
		return gson.toJson(responseRows);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUserPostDetails")
	public String getUserPostDetails() {
		// TODO do multiple iterations to get all users? pagination?
		List<User> users = gson.fromJson(RestAPIHelper.invokeGetAPI(usersAPIUrl), usersType);

		Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, user -> user));

		// TODO do multiple iterations to get all posts? pagination?
		List<UserPost> userPosts = gson.fromJson(RestAPIHelper.invokeGetAPI(userPostsAPIUrl), userPostsType);

		Map<Integer, UserPost> userPostMap = userPosts.stream()
				.collect(Collectors.toMap(UserPost::getPostId, user -> user));

		// Organize posts by user
		Map<Integer, List<CombinedUserPostModel>> userPostsPerUser = userPosts.stream().map(userPost -> {
			User user = userMap.get(userPost.user_id);
			if (user != null) {
				return new CombinedUserPostModel(userPost.id, userPost.user_id, userPost.title, userPost.body,
						user.name, user.email, user.gender, user.status);
			}
			return null;
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
		responseMap.put("userPosts", userPostsPerUser);
		responseMap.put("usersWithoutPosts", usersWithoutPosts);
		responseMap.put("usersWithPosts", usersWithPosts);
		responseMap.put("postsWithoutUsers", postsWithoutUsers);

		return gson.toJson(responseMap);
	}

}
