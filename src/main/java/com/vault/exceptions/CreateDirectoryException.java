package com.vault.exceptions;

import java.io.IOException;

public class CreateDirectoryException extends IOException {
	public CreateDirectoryException() {
		super();
	}
	
	public CreateDirectoryException(String message) {
		super(message);
	}
}
