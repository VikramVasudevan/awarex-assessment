package com.awarex.api.model;

public class UserPostPayload {
	public String name;
	public String email;
	public String gender;
	public String title;
	public String body;

	public UserPayload toUserPayload() {
		// Default user status is active
		return new UserPayload(name, email, gender, "active");
	}
}
