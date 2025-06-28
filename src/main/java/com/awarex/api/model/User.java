package com.awarex.api.model;

public class User {
	public int id;
	public String name;
	public String email;
	public String gender;
	public String status;

	public int getUserId() {
		return id;
	}

	public AwarexUser asAwarexuser() {
		return new AwarexUser(id, name, email, gender, status);
	}
}
