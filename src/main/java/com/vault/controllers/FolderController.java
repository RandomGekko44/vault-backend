package com.vault.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vault.dto.AddExistingFolderDTO;
import com.vault.dto.FolderDTO;
import com.vault.exceptions.FolderException;
import com.vault.exceptions.FolderNotFoundException;
import com.vault.models.Folder;
import com.vault.records.FolderView;
import com.vault.records.RootFolderView;
import com.vault.services.FileService;
import com.vault.services.FolderService;

@CrossOrigin("*")
@RestController
@RequestMapping("api/folder")
public class FolderController {
	
	@Autowired
	private FolderService folderService;
	
	@Autowired
	private FileService fileService;
	
	@GetMapping("/root-folders")
	public List<RootFolderView> getRootFolders() {
		return folderService.getRootFolders();
	}
	
	@GetMapping("/root-folder/{folderId}")
	public FolderView openRootFolder(@PathVariable String folderId) {
		return folderService.openRootFolder(folderId);
	}
	
	@GetMapping("/{parentFolderId}/subfolder/{subFolderName}")
	public FolderView openSubFolder(@PathVariable String parentFolderId, @PathVariable String subFolderName) {
		return folderService.openSubFolder(parentFolderId, subFolderName);
	}
	
	@PostMapping("/add")
	public void addFolder(@RequestBody FolderDTO folder) throws FolderException, FolderNotFoundException, IOException {
		folderService.addFolder(folder);
	}
	
	@PutMapping("/{folderId}/delete")
	public void deleteFolder(@PathVariable String folderId) throws FolderNotFoundException, IOException {
		folderService.deleteFolder(folderId);
	}
	
	@PutMapping("/{folderId}/rename")
	public void renameFolder(@PathVariable String folderId, @RequestParam String newName) {
		folderService.renameFolder(folderId, newName);
	}
	
	@PutMapping("/{sourceFolderId}/copy-folders")
	public void copyFolders(@PathVariable String sourceFolderId, @RequestParam String targetFolderId, @RequestParam Map<String, String> selectedFolders) {
		
	}
	
	public void moveFolder() {
		
	}
	
	@PutMapping("/{sourceFolderId}/copy-selected-items")
	public void copySelectedItems(@PathVariable String sourceFolderId, @RequestParam String targetFolderId, 
			@RequestParam(required = false) Map<String, String> selectedFiles, @RequestParam(required = false) Map<String, String> selectedFolders) throws IOException {
		
		boolean copyItemsOnSameFolder = (sourceFolderId.equals(targetFolderId)) ? true : false;
		
		if (copyItemsOnSameFolder) {
			if (!selectedFiles.isEmpty()) {
				fileService.copyFilesOnSameFolder(sourceFolderId, selectedFiles);
			}
			
			if (!selectedFolders.isEmpty()) {
				
			}
		}
		else {
			if (!selectedFiles.isEmpty()) {
				fileService.copyFiles(sourceFolderId, targetFolderId, selectedFiles);
			}
			
			if (!selectedFolders.isEmpty()) {
				folderService.copyFolders(sourceFolderId, targetFolderId, selectedFolders);
			}
		}
	}
	
	@GetMapping("/allFolders")
	public List<Folder> getFolders() {
		return folderService.getFolders();
	}
	
	/**
	 * Allows the addition of a folder's file tree to the database,
	 * so that the platform can show the folder and allow operations
	 * on the Client side such as adding, renaming or deleting files
	 * from the folder and its sub folders.
	 * @throws IOException 
	 */
	@PostMapping("/add-existing-folder")
	public void addExistingFolder(@RequestBody AddExistingFolderDTO folder) throws IOException {
		folderService.addExistingFolder(folder);
	}
	
	@ExceptionHandler(value = FolderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleFolderNotFoundException(FolderNotFoundException ex) {
		return new ErrorResponseException(HttpStatusCode.valueOf(404), ex);
	}
}
