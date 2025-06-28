package com.awarex.api.model;

public class CombinedUserPostModel {
	public CombinedUserPostModel(int id, int user_id, String title, String body, String name, String email,
			String gender, String status) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.body = body;
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.status = status;
	}

	public int id;
	public int user_id;
	public String title;
	public String body;

	public String name;
	public String email;
	public String gender;
	public String status;

	public int getPostId() {
		return id;
	}

	public int getUserId() {
		return user_id;
	}
}
