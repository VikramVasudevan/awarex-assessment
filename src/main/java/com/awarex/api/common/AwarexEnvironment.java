package com.awarex.api.common;

public class AwarexEnvironment {
	static AwarexEnvironment _env;

	public String accessToken;
	public String backendAPIBaseURI;

	public static AwarexEnvironment getInstance() {
		if (_env == null)
			_env = new AwarexEnvironment();

		return _env;
	}

	private AwarexEnvironment() {
		accessToken = System.getenv("GOREST_ACCESS_TOKEN");
		backendAPIBaseURI = System.getenv("GOREST_API_BASE_URI");
	}

}
