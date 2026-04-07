package com.vault.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vault.records.FolderItemsView;
import com.vault.services.FileService;

@RestController
@RequestMapping("api/folder")
@CrossOrigin("*")
public class FileController {
	
	@Autowired
	FileService fileService;
	
	@GetMapping("/dsadasd")
	public FolderItemsView showFolderItems(@PathVariable String folderId) {
		return fileService.showFolderItems(folderId);
	}
	
	/**
	 * @param folderId folder that contains the files
	 * @param filesToAdd files that will be added
	 * @param filesForReplace files that will replace other files
	 */
	@PostMapping("/{folderId}/add-files")
	public void addFiles(@PathVariable String folderId, @RequestParam(required = false) Map<String, MultipartFile> filesToAdd, 
			@RequestParam(required = false) Map<String, MultipartFile> filesForReplace) throws IOException {
		
		fileService.addFiles(folderId, filesToAdd, filesForReplace);
	}
	
	@DeleteMapping("/{folderId}/delete-files")
	public void deleteFile(@PathVariable String folderId, @RequestParam String[] files) throws IOException {
		fileService.deleteFile(folderId, files);
	}
	
	@PutMapping("/{folderId}/file/rename")
	public void renameFile(@PathVariable String folderId, @RequestParam String fileName, @RequestParam String newFileName) throws IOException {
		fileService.renameFile(folderId, fileName, newFileName);
	}
	
	/**
	 * @param sourceFolderId ID of the folder that contains the files
	 * @param targetFolderId ID of the folder where the files will be copied
	 * @param files its a Map that contains the files that will me copied, storing the file name's in the Key and the CopyOption in the Value, 
	 * indicating if the file will be added to the folder or replaced with another file. See the CopyOption enum for more details.
	 */
	@PutMapping("/{sourceFolderId}/copy-files")
	public void copyFiles(@PathVariable String sourceFolderId, @RequestParam String targetFolderId, @RequestParam Map<String, String> files) throws IOException {
		boolean copyFilesOnSameFolder = (sourceFolderId.equals(targetFolderId)) ? true : false;
		
		if (copyFilesOnSameFolder) {
			fileService.copyFilesOnSameFolder(sourceFolderId, files);
		}
		else {
			fileService.copyFiles(sourceFolderId, targetFolderId, files);
		}
	}
	
	/**
	 * @param sourceFolderId ID of the folder that contains the files
	 * @param targetFolderId ID of the folder where the files will be moved
	 * @param files its a Map that contains the files that will me moved, storing the file name's in the Key and the CopyOption in the Value, 
	 * indicating if the file will be added to the folder or replaced with another file. See the CopyOption enum for more details.
	 */
	@PutMapping("/{sourceFolderId}/move-files")
	public void moveFiles(@PathVariable String sourceFolderId, @RequestParam String targetFolderId, @RequestParam Map<String, String> files) throws IOException {
		fileService.moveFiles(sourceFolderId, targetFolderId, files);
	}
}
