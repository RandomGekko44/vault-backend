package com.vault.services.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vault.enums.ImageExtensions;
import com.vault.enums.CopyOption;
import com.vault.exceptions.FileException;
import com.vault.exceptions.FolderNotFoundException;
import com.vault.models.File;
import com.vault.models.Folder;
import com.vault.records.FolderItemsView;
import com.vault.repositories.FolderRepository;
import com.vault.services.FileService;
import com.vault.utils.FileType;
import com.vault.utils.ThumbnailGenerator;
import com.vault.utils.Thumbnails;
import com.vault.utils.TikaAnalysis;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	private FolderRepository folderRepository;
	
	@Autowired
	private ThumbnailGenerator thumbnailGenerator;
	
	@Autowired
	private Thumbnails thumbnails;
	
	@Autowired
	private TikaAnalysis tikaAnalysis;
	
	@Autowired
	private FileType fileType;
	
	@Override
	public FolderItemsView showFolderItems(String folderId) {
		Optional<FolderItemsView> files = folderRepository.findFilesById(folderId);
		if (files.isEmpty()) {
			throw new FolderNotFoundException();
		}
		return files.get();
	}
	
	@Override
	public void addFiles(String folderId, Map<String, MultipartFile> filesToAdd, Map<String, MultipartFile> filesForReplace) throws IOException {
		if (filesToAdd.isEmpty() && filesForReplace.isEmpty()) {
			throw new FileNotFoundException();
		}
		
		Optional<Folder> parentFolder = folderRepository.findById(folderId);
		if (parentFolder.isEmpty()) {
			throw new FolderNotFoundException("Folder " + folderId + "not found");
		}
		
		//Manages the files that are send
		class FilesProcessor {
			private Path filePath;
			
			/**
			 * @param isReplace	Indicates if the files are to be replaced with others
			 */
			public void awake(Map<String, MultipartFile> files, boolean isReplace) throws IOException {
				for (Map.Entry<String, MultipartFile> file : files.entrySet()) {
					filePath = Paths.get(parentFolder.get().getMetadata().getPath() + "/" + file.getKey());
					
					if (Files.exists(filePath) && isReplace == false) {
						throw new FileException("File " + file.getKey() + " already exist");
					}
					
					File newFile = new File();
					newFile.setId(UUID.randomUUID().toString());
					newFile.setName(file.getKey());
					newFile.getMetadata().setSize(file.getValue().getSize());
					newFile.getMetadata().setCreatedAt(LocalDateTime.now());
					newFile.getMetadata().setModifiedAt(LocalDateTime.now());
					
					// TODO: CHECK FILE TYPE AND ASSIGN IT TO THE NEW FILE
					
					if (isReplace) {
						replace(newFile);
					}
					else {
						add(newFile);
					}
					
					//Storing the uploaded file
					try {
						file.getValue().transferTo(filePath);
					} catch (IllegalStateException | IOException e) {
						throw new IOException("Failed to store the file on the path " + filePath + ". " + e.getMessage());
					}
					
					if (fileType.isImage(newFile)) {
						thumbnailGenerator.createThumbnail(folderId, filePath.toFile(), newFile.getId());
					}
				}
			}
			
			private void add(File file) {
				parentFolder.get().getFiles().add(file);
				parentFolder.get().getMetadata().increaseFileAmountByOne();
				parentFolder.get().getMetadata().sumSize(file.getMetadata().getSize());
				parentFolder.get().getMetadata().setModifiedAt(file.getMetadata().getModifiedAt());
			}
			
			private void replace(File file) throws IOException {
				//Deleting from the folder's file list the file that will be replaced
				Optional<File> fileToReplace = parentFolder.get().getFiles().stream()
						.filter(fileOnList -> fileOnList.getName().equals(file.getName())).findFirst();
				
				parentFolder.get().getFiles().removeIf(fileOnList -> fileOnList.getName().equals(file.getName()));
				parentFolder.get().getMetadata().subtractSize(fileToReplace.get().getMetadata().getSize());
				
				if (fileType.isImage(file)) {
					thumbnails.deleteThumbnail(folderId, file.getId());
				}
				
				try {
					Files.delete(filePath);
				} catch (IOException e) {
					throw new IOException("Failed to delete the file " + fileToReplace.get().getName());
				}
				
				//Adding the new file to the folder's file list
				parentFolder.get().getFiles().add(file);
				parentFolder.get().getMetadata().sumSize(file.getMetadata().getSize());
				parentFolder.get().getMetadata().setModifiedAt(file.getMetadata().getModifiedAt());
			}
		}
		
		FilesProcessor fileProcessor = new FilesProcessor();
		if (!filesToAdd.isEmpty()) {
			fileProcessor.awake(filesToAdd, false);
		}
		
		if (!filesForReplace.isEmpty()) {
		    fileProcessor.awake(filesForReplace, true);
		}
		
		folderRepository.save(parentFolder.get());
	}

	@Override
	public void deleteFile(String folderId, String[] files) throws IOException {
		Optional<Folder> parentFolder = folderRepository.findById(folderId);
		if (parentFolder.isEmpty()) {
			throw new FolderNotFoundException("Folder " + folderId + "not found");
		}
		
		for (String file : files) {
			if (!parentFolder.get().getFiles().stream()
					.anyMatch(fileOnList -> fileOnList.getName().equals(file))) {
				
				throw new FileNotFoundException("File not found on folder with ID " + folderId);
			}
			
			Path filePath = Paths.get(parentFolder.get().getMetadata().getPath()).resolve(file);
			if (!Files.exists(filePath)) {		
				throw new IOException();
			}
			
			Optional<File> fileData = parentFolder.get().getFiles().stream()
					.filter(fileOnList -> fileOnList.getName().equals(file)).findFirst();
			
			thumbnails.deleteThumbnail(folderId, fileData.get().getId());
			Files.delete(filePath);
			
			parentFolder.get().getFiles()
				.removeIf(fileOnList -> fileOnList.getName().equals(file));
			parentFolder.get().getMetadata().decreaseFileAmountByOne();
			parentFolder.get().getMetadata().subtractSize(fileData.get().getMetadata().getSize());
			parentFolder.get().getMetadata().setModifiedAt(LocalDateTime.now());
		}
		
		folderRepository.save(parentFolder.get());
	}

	@Override
	public void renameFile(String folderId, String fileName, String newFileName) throws IOException {
		Optional<Folder> parentFolder = folderRepository.findById(folderId);
		if (parentFolder.isEmpty()) {
			throw new FolderNotFoundException("Folder " + folderId + "not found");
		}
		
		Path filePath = Paths.get(parentFolder.get().getMetadata().getPath()).resolve(fileName);
		if (!Files.exists(filePath)) {		
			throw new IOException();
		}
		
		Optional<File> fileData = parentFolder.get().getFiles().stream()
				.filter(file -> file.getName().equals(fileName)).findFirst();
		if (fileData.isEmpty()) {
			throw new FileNotFoundException("File not found on folder with ID " + folderId);
		}
		
		fileData.get().setName(newFileName);
		fileData.get().getMetadata().setModifiedAt(LocalDateTime.now());
		parentFolder.get().getMetadata().setModifiedAt(LocalDateTime.now());
		
		//Renaming the stored file
		Files.move(filePath, filePath.resolveSibling(newFileName));
		
		folderRepository.save(parentFolder.get());
	}

	@Override
	public void copyFiles(String sourceFolderId, String targetFolderId, Map<String, String> files) throws IOException {
		Optional<Folder> sourceFolder = folderRepository.findById(sourceFolderId);
		if (sourceFolder.isEmpty()) {
			throw new FolderNotFoundException("Source folder " + sourceFolderId + " not found");
		}
		
		Optional<Folder> targetFolder = folderRepository.findById(sourceFolderId);
		if (targetFolder.isEmpty()) {
			throw new FolderNotFoundException("Target folder " + sourceFolderId + " not found");
		}
		
		Path targetFolderPath = Paths.get(targetFolder.get().getMetadata().getPath());
		
		class FilesProcessor {
			private Path filePath;
			
			public void awake() throws IOException {
				for (Map.Entry<String, String> file : files.entrySet()) {
					filePath = Paths.get(sourceFolder.get().getMetadata().getPath() + "/" + file.getKey());
					
					if (file.getValue().equals(CopyOption.ADD.getOption())) {
						add(file.getKey());
					}
					
					if (file.getValue().equals(CopyOption.REPLACE.getOption())) {
						replace(file.getKey());
					}
				}
			}
			
			private void add(String fileName) throws IOException {
				if (Files.exists(filePath)) {
					throw new FileException("File " + fileName + " already exist");
				}
				
				try {
					Files.copy(filePath, targetFolderPath, StandardCopyOption.COPY_ATTRIBUTES);
				} catch (IOException e) {
					throw new IOException("Failed to copy the file " + fileName);
				}
				
				Optional<File> fileData = sourceFolder.get().getFiles().stream()
						.filter(fileOnList -> fileOnList.getName().equals(fileName)).findFirst();
				
				targetFolder.get().getFiles().addLast(fileData.get());
				targetFolder.get().getMetadata().increaseFileAmountByOne();
				targetFolder.get().getMetadata().setModifiedAt(LocalDateTime.now());
				targetFolder.get().getMetadata().sumSize(fileData.get().getMetadata().getSize());
				
				if (fileType.isImage(fileData.get())) {
					thumbnails.moveThumbnail(sourceFolderId, targetFolderId, fileData.get().getId());
				}
			}
			
			private void replace(String fileName) throws IOException {
				try {
					Files.copy(filePath, targetFolderPath, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new IOException("Failed to replace the file " + fileName);
				}
				
				Optional<File> newFileData = sourceFolder.get().getFiles().stream()
						.filter(fileOnList -> fileOnList.getName().equals(fileName)).findFirst();
				
				Optional<File> oldFileData = targetFolder.get().getFiles().stream()
						.filter(fileOnList -> fileOnList.getName().equals(fileName)).findFirst();
				
				//Deleting from the target folder's document the file that will be replaced
				targetFolder.get().getFiles().removeIf(fileOnList -> fileOnList.getName().equals(fileName));
				targetFolder.get().getMetadata().subtractSize(oldFileData.get().getMetadata().getSize());
				
				//Adding the new file to the target folder's document
				targetFolder.get().getFiles().addLast(newFileData.get());
				targetFolder.get().getMetadata().sumSize(newFileData.get().getMetadata().getSize());
				targetFolder.get().getMetadata().setModifiedAt(LocalDateTime.now());
				
				if (fileType.isImage(newFileData.get())) {
					thumbnails.deleteThumbnail(targetFolderId, oldFileData.get().getId());
					thumbnails.moveThumbnail(sourceFolderId, targetFolderId, newFileData.get().getId());
				}
			}
		}
		
		FilesProcessor filesProcessor = new FilesProcessor();
		filesProcessor.awake();
		folderRepository.save(targetFolder.get());
	}
	
	@Override
	public void copyFilesOnSameFolder(String folderId, Map<String, String> files) throws IOException {
		Optional<Folder> folder = folderRepository.findById(folderId);
		if (folder.isEmpty()) {
			throw new FolderNotFoundException("Folder " + folderId + " not found");
		}
		
		Path folderPath = Paths.get(folder.get().getMetadata().getPath());
		
		class FilesProcessor {
			private Path filePath;
			
			public void awake() throws IOException {
				for (Map.Entry<String, String> file : files.entrySet()) {
					filePath = Paths.get(folder.get().getMetadata().getPath() + "/" + file.getKey());
					add(file.getKey());
				}
			}
			
			private void add(String fileName) throws IOException {
				String newFileName = "";
				Path newFilePath = null;
				for (int i=0; newFileName.isEmpty(); i++) {
					if (Files.notExists(folderPath.resolve(fileName + " (" + i + ")"))) {
						newFileName = fileName + " (" + i + ")";
						newFilePath = folderPath.resolve(newFileName);
					}
				}
				
				try {
					Files.copy(filePath, newFilePath, StandardCopyOption.COPY_ATTRIBUTES);
				} catch (IOException e) {
					throw new IOException("Failed to copy the file " + fileName);
				}
				
				Optional<File> fileData = folder.get().getFiles().stream()
						.filter(fileOnList -> fileOnList.getName().equals(fileName)).findFirst();
				
				fileData.get().setName(newFileName);
				folder.get().getFiles().addLast(fileData.get());
				folder.get().getMetadata().increaseFileAmountByOne();
				folder.get().getMetadata().setModifiedAt(LocalDateTime.now());
				folder.get().getMetadata().sumSize(fileData.get().getMetadata().getSize());
				
				if (fileType.isImage(fileData.get())) {
					final String newFileId = UUID.randomUUID().toString();
					thumbnails.copyThumbnail(
						folderId,
						fileData.get().getId(), 
						newFileId
					);
					fileData.get().setId(folderId);
				}
				else {
					fileData.get().setId(UUID.randomUUID().toString());
				}
			}
		}
		
		FilesProcessor filesProcessor = new FilesProcessor();
		filesProcessor.awake();
		folderRepository.save(folder.get());
	}

	@Override
	public void moveFiles(String sourceFolderId, String targetFolderId, Map<String, String> files) throws IOException {
		copyFiles(sourceFolderId, targetFolderId, files);
		
		Optional<Folder> sourceFolder = folderRepository.findById(sourceFolderId);
		//Deletes from the storage and the database the files that were moved
		for (Map.Entry<String, String> file : files.entrySet()) {
			Optional<File> fileData = sourceFolder.get().getFiles().stream()
					.filter(fileOnList -> fileOnList.getName().equals(file.getKey())).findFirst();
			
			sourceFolder.get().getFiles().removeIf(fileOnList -> fileOnList.getName().equals(file.getKey()));
			sourceFolder.get().getMetadata().subtractSize(fileData.get().getMetadata().getSize());
			sourceFolder.get().getMetadata().decreaseFileAmountByOne();
			sourceFolder.get().getMetadata().setModifiedAt(LocalDateTime.now());
			
			Path filePath = Paths.get(sourceFolder.get().getMetadata().getPath()).resolve(file.getKey());
			Files.delete(filePath);
		}
		
		folderRepository.save(sourceFolder.get());
	}
}
