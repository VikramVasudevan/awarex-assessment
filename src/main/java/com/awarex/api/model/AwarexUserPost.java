package com.awarex.api.model;

public class AwarexUserPost {
	public AwarexUserPost(int id, int userId, String postTitle, String postBody) {
		super();
		this.id = id;
		this.userId = userId;
		this.postTitle = postTitle;
		this.postBody = postBody;
	}

	public int id;
	public int userId;
	public String postTitle;
	public String postBody;
}
