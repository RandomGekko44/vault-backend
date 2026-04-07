package com.vault.models;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Field.Write;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document("folders")
public class Folder {
	@Id
	private String id;
	
	private String name;	
	private FolderMetadata metadata;
	private List<String> subfolders;
	private List<File> files;
	
	@JsonIgnore
	@Field(name = "parent_folder", write = Write.ALWAYS)
	@Indexed(partialFilter = "{ 'parent_folder' : null }")
	@DBRef
	private Folder parentFolder;

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

	public FolderMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FolderMetadata metadata) {
		this.metadata = metadata;
	}

	public List<String> getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(List<String> subfolders) {
		this.subfolders = subfolders;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public Folder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
	}
}
