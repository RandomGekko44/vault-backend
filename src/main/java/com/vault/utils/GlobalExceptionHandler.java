package com.vault.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.vault.exceptions.CreateDirectoryException;
import com.vault.exceptions.FileException;
import com.vault.exceptions.FolderException;
import com.vault.exceptions.FolderNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(FolderException.class)
	public ProblemDetail handleFolderException(FolderException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
		problemDetail.setTitle("FolderException");
		problemDetail.setInstance(URI.create("folder"));
		return problemDetail;
	}
	
	@ExceptionHandler(FolderNotFoundException.class)
	public ProblemDetail handleFolderNotFoundException(FolderNotFoundException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
		problemDetail.setInstance(URI.create("folder"));
		return problemDetail;
	}
	
	@ExceptionHandler(CreateDirectoryException.class)
	public ProblemDetail handleCreateDirectoryException(CreateDirectoryException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "");
		problemDetail.setInstance(URI.create("folder"));
		return problemDetail;
	}
	
	@ExceptionHandler(FileException.class)
	public ProblemDetail handleFileException(FileException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "");
		problemDetail.setInstance(URI.create("file"));
		return problemDetail;
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	public ProblemDetail handleFileNotFoundException(FileNotFoundException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
		problemDetail.setInstance(URI.create("file"));
		return problemDetail;
	}
	
	@ExceptionHandler(IOException.class)
	public ProblemDetail handleIOException(IOException e) {
		ProblemDetail problemDetail 
			= ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
		problemDetail.setInstance(URI.create("file"));
		return problemDetail;
	}
}
