package com.vault.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.vault.dto.AddExistingFolderDTO;
import com.vault.dto.FolderDTO;
import com.vault.exceptions.FolderException;
import com.vault.exceptions.FolderNotFoundException;
import com.vault.models.Folder;
import com.vault.records.FolderView;
import com.vault.records.RootFolderView;

public interface FolderService {
	public List<RootFolderView> getRootFolders();
	
	public FolderView openRootFolder(String folderId);
	
	public FolderView openSubFolder(String parentFolderId, String subFolderName);
	
	public void addFolder(FolderDTO folder) throws FolderException, FolderNotFoundException, IOException;
	
	public void deleteFolder(String folderId) throws FolderNotFoundException, IOException;
	
	public void renameFolder(String folderId, String newName);
	
	public void copyFolders(String sourceFolderId, String targetFolderId, Map<String, String> selectedFolders);
	
	public List<Folder> getFolders();
	
	public void addExistingFolder(AddExistingFolderDTO folder) throws IOException;
}
