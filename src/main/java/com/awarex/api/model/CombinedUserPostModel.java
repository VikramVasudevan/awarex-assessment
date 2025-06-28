package com.awarex.api.model;

public class CombinedUserPostModel {
	public CombinedUserPostModel(int id, int user_id, String title, String body, String name, String email,
			String gender, String status) {
		super();
		this.id = id;
		this.userId = user_id;
		this.postTitle = title;
		this.postBody = body;
		this.userName = name;
		this.userEmail = email;
		this.userGender = gender;
		this.userStatus = status;
	}

	public int id;
	public int userId;
	public String postTitle;
	public String postBody;

	public String userName;
	public String userEmail;
	public String userGender;
	public String userStatus;

	public int getPostId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}
}
