package com.awarex.api.userposts;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.awarex.api.common.RestAPIHelper;
import com.awarex.api.model.CombinedUserPostModel;
import com.awarex.api.model.User;
import com.awarex.api.model.UserPost;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
		List responseRows = new Gson().fromJson(RestAPIHelper.invokeGetAPI("https://gorest.co.in/public/v2/posts"),
				List.class);
		return new Gson().toJson(responseRows);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getUserPostDetails")
	public String getUserPostDetails() {
		Type usersType = new TypeToken<List<User>>() {
		}.getType();
		Type userPostsType = new TypeToken<List<UserPost>>() {
		}.getType();

		// TODO do multiple iterations to get all users? pagination?
		List<User> users = new Gson().fromJson(RestAPIHelper.invokeGetAPI("https://gorest.co.in/public/v2/users"),
				usersType);

		Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, user -> user));

		// TODO do multiple iterations to get all posts? pagination?
		List<UserPost> userPosts = new Gson()
				.fromJson(RestAPIHelper.invokeGetAPI("https://gorest.co.in/public/v2/posts"), userPostsType);
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

		Map responseMap = new HashMap();
		responseMap.put("userPosts", userPostsPerUser);
		responseMap.put("usersWithoutPosts", 100);
		responseMap.put("usersWithPosts", 125);
		responseMap.put("postsWithoutUsers", 23);

		return new Gson().toJson(responseMap);
	}

}
