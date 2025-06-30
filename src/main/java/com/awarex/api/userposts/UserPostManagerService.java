package com.awarex.api.userposts;

import java.lang.reflect.Type;
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
import com.awarex.api.model.ResponseErrorModel;
import com.awarex.api.model.User;
import com.awarex.api.model.UserPost;
import com.awarex.api.model.UserPostDetailsModel;
import com.awarex.api.model.UserPostPayload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@SwaggerDefinition(info = @Info(title = "Awarex APIs", description = "APIS to manage user posts", termsOfService = "terms.html", contact = @Contact(name = "Awarex Tech Support", email = "support@awarex.com", url = "https://awarex.com"), version = "V1", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0")), produces = {
		"application/json",
		"text/xml" }, schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS }, tags = {
				@Tag(name = "User Posts", description = "Get and post data using GOREST Backend API") }, externalDocs = @ExternalDocs(value = "About Awarex", url = "https://awarex.com"), securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {
						@ApiKeyAuthDefinition(in = ApiKeyLocation.HEADER, description = "Authentication using bearer token", name = "Authorization", key = "Bearer") }))
@Api(value = "/userposts", tags = "User Posts")
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

	@ApiOperation(value = "Create new post", notes = "Create a new post. If user email exists, use the existing user id, else create the new user and use that id.", response = UserPostDetailsModel.class, produces = "application/json", consumes = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully processed", response = UserPostDetailsModel.class),
			@ApiResponse(code = 422, message = "Unprocessable Entity", response = ResponseErrorModel.class),
			@ApiResponse(code = 400, message = "Invalid parameters supplied"),
			@ApiResponse(code = 401, message = "Not authorized") })
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

	@ApiOperation(value = "Get User Post Details", notes = "Fetch user posts organized by userid. Provide additionl data quality insights such as users with posts, users without posts, posts without users.", response = UserPostDetailsModel.class, produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully processed", response = UserPostDetailsModel.class),
			@ApiResponse(code = 400, message = "Invalid parameters supplied"),
			@ApiResponse(code = 404, message = "No data found"), @ApiResponse(code = 401, message = "Not authorized") })
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
