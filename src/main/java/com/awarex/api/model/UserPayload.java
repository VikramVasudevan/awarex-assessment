package com.awarex.api.model;

public class UserPayload {
	public UserPayload(String name, String email, String gender, String status) {
		super();
		this.name = name;
		this.email = email;
		this.gender = gender;
		this.status = status;
	}
	public String name;
	public String email;
	public String gender;
	public String status;
}
