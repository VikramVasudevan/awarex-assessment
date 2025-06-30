package com.awarex.api.model;

import java.util.Map;

public class UserPostDetailsModel {
	public int usersWithoutPosts;
	public int totalUsers;
	public int usersWithPosts;
	public int postsWithoutUsers;
	public int totalUserPosts;
	public Map<String, User> users;
	public Map<String, CombinedUserPostModel> userPosts;
}
