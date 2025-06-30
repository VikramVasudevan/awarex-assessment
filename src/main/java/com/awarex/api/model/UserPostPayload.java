package com.awarex.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "This is the request payload for writeAPost API.", value = "User Post Payload")
public class UserPostPayload {
	@ApiModelProperty(example = "Vikram Vasudevan", dataType = "String", required = true)
	public String name;
	@ApiModelProperty(example = "Vikram.Vasudevan@ekahaa.com", dataType = "String", required = true)
	public String email;
	@ApiModelProperty(example = "Male", dataType = "String", required = true)
	public String gender;
	@ApiModelProperty(example = "The story of an entrepreneur", dataType = "String", required = true)
	public String title;
	@ApiModelProperty(example = "This post is about an entrepreneur who rose  from humble backgrounds to become one of the world's greatest icons.", dataType = "String", required = true)
	public String body;

	public UserPayload toUserPayload() {
		// Default user status is active
		return new UserPayload(name, email, gender, "active");
	}
}
