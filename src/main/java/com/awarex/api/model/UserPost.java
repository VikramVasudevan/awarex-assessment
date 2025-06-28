package com.awarex.api.model;

public class UserPost {
	public int id;
	public int user_id;
	public String title;
	public String body;

	public int getPostId() {
		return id;
	}

	public int getUserId() {
		return user_id;
	}

	public AwarexUserPost asAwarexUserPost() {
		return new AwarexUserPost(id, user_id, title, body);
	}
}
