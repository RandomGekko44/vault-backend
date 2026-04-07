package com.vault.models;

import org.springframework.data.mongodb.core.mapping.Field;

public class File {
	private String id;
	private String name;
	
	private FileMetadata metadata = new FileMetadata();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FileMetadata metadata) {
		this.metadata = metadata;
	}
}
