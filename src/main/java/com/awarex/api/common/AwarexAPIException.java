package com.awarex.api.common;

public class AwarexAPIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AwarexAPIException() {
		// TODO Auto-generated constructor stub
		super();
	}

	public AwarexAPIException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}

	public AwarexAPIException(Exception e) {
		super(e);
	}

	public AwarexAPIException(String message, Exception e) {
		super(message, e);
	}

}
