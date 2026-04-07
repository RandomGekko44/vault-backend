package com.vault.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Base64;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vault.dto.AddExistingFolderDTO;
import com.vault.dto.FolderDTO;
import com.vault.enums.ImageExtensions;
import com.vault.exceptions.FolderException;
import com.vault.exceptions.FolderNotFoundException;
import com.vault.models.File;
import com.vault.models.Folder;
import com.vault.records.FolderView;
import com.vault.records.ParentFolderAndNameView;
import com.vault.records.RootFolderView;
import com.vault.repositories.FolderRepository;
import com.vault.services.FolderService;
import com.vault.utils.FileType;
import com.vault.utils.ThumbnailGenerator;
import com.vault.utils.Thumbnails;
import com.vault.utils.TikaAnalysis;

@Service
public class FolderServiceImpl implements FolderService {

	@Autowired
	private FolderRepository folderRepository;
	
	@Autowired
	private Thumbnails thumbnails;
	
	@Autowired
	private ThumbnailGenerator thumbnailGenerator;
	
	@Autowired
	private TikaAnalysis tikaAnalysis;
	
	@Autowired
    private FileType fileType;
	
	@Override
	public List<RootFolderView> getRootFolders() {
		return folderRepository.findByParentFolderIsNull();
	}
	
	@Override
	public FolderView openRootFolder(String folderId) {
		return folderRepository.findRootFolderById(folderId);
	}
	
	@Override
	public FolderView openSubFolder(String parentFolderId, String subFolderName) {
		return folderRepository.findByParentFolderAndName(parentFolderId, subFolderName);
	}
	
	@Override
	public void addFolder(FolderDTO folder) throws FolderException, FolderNotFoundException, IOException {
		Folder folderInsert = new Folder();
		folderInsert.setName(folder.name());
		folderInsert.setMetadata(folder.metadata());
		
		folderInsert.getMetadata().setPathBase64(
			Base64.getEncoder().encodeToString(
				folderInsert.getMetadata().getPath().getBytes()
			)
		);
		
		//Checking if the folder already exist
		Path folderPath = Paths.get(folderInsert.getMetadata().getPath());
		if (!Files.isDirectory(folderPath)) {
			try {
				Files.createDirectory(Paths.get(folderInsert.getMetadata().getPath()));
			} catch (IOException e) {
				throw new FolderException("Failed to create folder: " + e.getMessage());
			}
		}
		else {
			throw new IOException("Folder already exist: " + folderPath);
		}
		
		/*
		 * Retrieving the parent folder to make a relationship between the new folder and
		 * the parent folder
		*/
		Path parentFolderPath = Path.of(folderInsert.getMetadata().getPath());
		parentFolderPath = parentFolderPath.normalize().getParent();
		
		Optional<Folder> parentFolder = folderRepository.findByMetadataPathBase64(
			Base64.getEncoder().encodeToString(parentFolderPath.toString().getBytes())
		);
		
		if (parentFolder.isPresent()) {
			parentFolder.get().getSubfolders().add(folderInsert.getName());
			folderRepository.save(parentFolder.get());
			folderInsert.setParentFolder(parentFolder.get());
		}
		
		folderRepository.insert(folderInsert);
	}
	
	@Override
	public void deleteFolder(String folderId) throws FolderNotFoundException, IOException {
		ParentFolderAndNameView data = folderRepository.findParentFolderAndNameById(folderId);
		
		if (data != null) {
			Folder parentFolder = data.parentFolder();
			String deletedFolderName = data.name();
			
			folderRepository.deleteById(folderId);
			thumbnails.deleteThumbnailFolders(folderId);
			
			parentFolder.getSubfolders().remove(deletedFolderName);
			folderRepository.save(parentFolder);
		}
		else {
			throw new FolderNotFoundException("Folder not found");
		}
	}
	
	@Override
	public void renameFolder(String folderId, String newName) {
		if (newName.isBlank() || newName.isEmpty()) {
			throw new IllegalCharsetNameException("The name cannot be blank");
		}
		
		if (newName.contains("/")) {
			throw new IllegalCharsetNameException("Character " + "/" + " is illegal");
		}
		
		Optional<Folder> folder = folderRepository.findById(folderId);
		if (folder.isEmpty()) {
			throw new FolderNotFoundException("Folder not found");
		}
		
		folder.get().setName(newName);
		folderRepository.save(folder.get());
	}
	
	@Override
	public void copyFolders(String sourceFolderId, String targetFolderId, Map<String, String> selectedFolders) {
		Optional<Folder> sourceFolder = folderRepository.findById(sourceFolderId);
		if (sourceFolder.isEmpty()) {
			throw new FolderNotFoundException("Source folder " + sourceFolderId + " not found");
		}
		
		Optional<Folder> targetFolder = folderRepository.findById(sourceFolderId);
		if (targetFolder.isEmpty()) {
			throw new FolderNotFoundException("Target folder " + sourceFolderId + " not found");
		}
		
		Path targetFolderPath = Paths.get(targetFolder.get().getMetadata().getPath());
		
		class FoldersProcessor {
			public void awake() {
				
			}
			
			private void add() {
				
			}
			
			private void replace() {
				
			}
		}
	}
	
	@Override
	public List<Folder> getFolders() {
		return folderRepository.findAll();
	}
	
	@Transactional
	@Override
	public void addExistingFolder(AddExistingFolderDTO existingFolder) throws IOException {
		Folder folder = new Folder();
		
		folder.setName(existingFolder.name());
		folder.setMetadata(existingFolder.metadata());
		folder.setSubfolders(existingFolder.subfolders());
		folder.setFiles(existingFolder.files());
		
		folder.getMetadata().setPathBase64(
			Base64.getEncoder().encodeToString(
				folder.getMetadata().getPath().getBytes()
			)
		);
		
		boolean hasImages = false;
		for (File file : folder.getFiles()) {
			file.setId(UUID.randomUUID().toString());
			
			//Checking file type
			Path filePath = Paths.get(folder.getMetadata().getPath(), file.getName());
			InputStream stream = new FileInputStream(filePath.toFile());
			file.getMetadata().setType(
				tikaAnalysis.detectFileType(stream)
			);
			
			if (!hasImages && fileType.isImage(file)) {
				hasImages = true;
			}
		}
		
		//Searching for the parent folder
		Path parentFolderPath = Path.of(existingFolder.metadata().getPath());
		parentFolderPath = parentFolderPath.normalize().getParent();
		
		Optional<Folder> parentFolder = folderRepository.findByMetadataPathBase64(
			Base64.getEncoder().encodeToString(parentFolderPath.toString().getBytes())
		);
		
		if (parentFolder.isPresent()) {
			folder.setParentFolder(parentFolder.get());
		}
		
		if (hasImages) {
			Folder savedFolder = folderRepository.insert(folder);
			thumbnailGenerator.createThumbnails(savedFolder);
		}
		else {
			folderRepository.insert(folder);
		}
	}
}
