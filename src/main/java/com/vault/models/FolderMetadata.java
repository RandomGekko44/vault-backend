package com.vault.models;

import org.springframework.data.mongodb.core.mapping.Field;

public class FolderMetadata extends Metadata {
	@Field("file_amount")
	private Long fileAmount;
	
	@Field("path")
	private String path; //Path of the folder on storage drive
	
	/**
	 * This field is used to avoid path comparison problems
	 * when dealing with paths that have non english
	 * characters. It makes the search of a folder's
	 * parent folder easier.
	 * 
	 * This field can also be used as an ID,
	 * because every path is unique.
	 */
	@Field("path_base64")
	private String pathBase64;

	public Long getFileAmount() {
		return fileAmount;
	}

	public void setFileAmount(Long fileAmount) {
		this.fileAmount = fileAmount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathBase64() {
		return pathBase64;
	}

	public void setPathBase64(String pathBase64) {
		this.pathBase64 = pathBase64;
	}
	
	public void increaseFileAmountByOne() {
		this.fileAmount++;
	}
	
	public void decreaseFileAmountByOne() {
		this.fileAmount--;
	}
}
