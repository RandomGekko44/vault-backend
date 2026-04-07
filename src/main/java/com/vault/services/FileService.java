package com.vault.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.vault.records.FolderItemsView;

public interface FileService {
	public FolderItemsView showFolderItems(String folderId);
	
	public void addFiles(String folderId, Map<String, MultipartFile> filesToAdd, Map<String, MultipartFile> filesForReplace) throws IOException;
	
	public void deleteFile(String folderId, String[] files) throws IOException;
	
	public void renameFile(String folderId, String fileName, String newFileName) throws IOException;
	
	public void copyFiles(String sourceFolderId, String targetFolderId, Map<String, String> files) throws IOException;
	
	public void copyFilesOnSameFolder(String folderId, Map<String, String> files) throws IOException;
	
	public void moveFiles(String sourceFolderId, String targetFolderId, Map<String, String> files) throws IOException;
}
