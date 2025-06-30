package com.awarex.api.model;

import java.util.List;

public class ResponseErrorModel {
	static class ErrorDetails {
		public String field;
		public String message;
	}

	public String developerErrorMessage;
	public List<ErrorDetails> errors;

}
