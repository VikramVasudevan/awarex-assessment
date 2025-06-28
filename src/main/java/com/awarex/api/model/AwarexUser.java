package com.awarex.api.model;

public class AwarexUser {
	public AwarexUser(int id, String userName, String userEmail, String userGender, String userStatus) {
		super();
		this.id = id;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userGender = userGender;
		this.userStatus = userStatus;
	}
	public int id;
	public String userName;
	public String userEmail;
	public String userGender;
	public String userStatus;
}
