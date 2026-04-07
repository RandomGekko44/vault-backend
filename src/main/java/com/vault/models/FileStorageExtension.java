package com.vault.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
 * This document serves as a backup that will be used when a Folder's JSON size reaches 16MB (MongoDB JSON size limit). 
*/

@Document("file_storage_extensions")
public class FileStorageExtension {
	@Id
	private String id;
	
	@DBRef
	private Folder folder;
	
	private List<File> files;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
}
