package com.vault.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vault.configs.AppConfig;
import com.vault.enums.ThumbnailQuality;

@Component
public class Thumbnails {
	@Autowired
	private AppConfig appConfig;
	
	private final String thumbnailFileExtension = ".webp";
	
	public void createThumbnailFolders(String folderId) throws IOException {		
		//Creating the folders that will store the thumbnails
		try {
            Files.createDirectory(appConfig.getLowQualityThumbnailsPath().resolve(folderId));
            Files.createDirectory(appConfig.getHighQualityThumbnailsPath().resolve(folderId));
        } catch (IOException e) {
            throw new IOException("Failed to create directory: " + e.getMessage());
        }
	}
	
	public void deleteThumbnailFolders(String folderId) throws IOException {		
		try {
            Files.delete(appConfig.getLowQualityThumbnailsPath().resolve(folderId));
            Files.delete(appConfig.getHighQualityThumbnailsPath().resolve(folderId));
        } catch (IOException e) {
            throw new IOException("Failed to delete directory: " + e.getMessage());
        }
	}
	
	public void deleteThumbnail(String folderId, String thumbnailName) throws IOException {		
		try {
			Files.delete(appConfig.getLowQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName.concat(thumbnailFileExtension)));
			Files.delete(appConfig.getHighQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName.concat(thumbnailFileExtension)));
		} catch (IOException e) {
			throw new IOException("Failed to delete file: " + e.getMessage());
		}
	}
	
	public void moveThumbnail(String sourceFolderId, String targetFolderId, String thumbnailName) throws IOException {
		Path lqThumbPath = appConfig.getLowQualityThumbnailsPath().resolve(sourceFolderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		Path hqThumbPath = appConfig.getHighQualityThumbnailsPath().resolve(sourceFolderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		
		Path targetLqThumbFolderPath = appConfig.getLowQualityThumbnailsPath().resolve(targetFolderId);
		Path targetHqThumbFolderPath = appConfig.getHighQualityThumbnailsPath().resolve(targetFolderId);
		
		try {
			Files.move(lqThumbPath, targetLqThumbFolderPath);
			Files.move(hqThumbPath, targetHqThumbFolderPath);
		} catch (IOException e) {
			throw new IOException("Failed to move file: " + e.getMessage());
		}
	}
	
	public void copyThumbnail(String sourceFolderId, String targetFolderId, String thumbnailName) throws IOException {
		Path lqThumbPath = appConfig.getLowQualityThumbnailsPath().resolve(sourceFolderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		Path hqThumbPath = appConfig.getHighQualityThumbnailsPath().resolve(sourceFolderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		
		Path targetLqThumbFolderPath = appConfig.getLowQualityThumbnailsPath().resolve(targetFolderId);
		Path targetHqThumbFolderPath = appConfig.getHighQualityThumbnailsPath().resolve(targetFolderId);
		
		try {
			Files.copy(lqThumbPath, targetLqThumbFolderPath);
			Files.copy(hqThumbPath, targetHqThumbFolderPath);
		} catch (IOException e) {
			throw new IOException("Failed to copy file: " + e.getMessage());
		}
	}
	
	public void copyThumbnailOnSameFolder(String folderId, String thumbnailName, String newThumbnailName) throws IOException {
		Path lqThumbPath = appConfig.getLowQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		Path hqThumbPath = appConfig.getHighQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName.concat(thumbnailFileExtension));
		
		Path targetLqThumbFolderPath = appConfig.getLowQualityThumbnailsPath().resolve(folderId + "/" + newThumbnailName.concat(thumbnailFileExtension));
		Path targetHqThumbFolderPath = appConfig.getHighQualityThumbnailsPath().resolve(folderId + "/" + newThumbnailName.concat(thumbnailFileExtension));
		
		try {
			Files.copy(lqThumbPath, targetLqThumbFolderPath);
			Files.copy(hqThumbPath, targetHqThumbFolderPath);
		} catch (IOException e) {
			throw new IOException("Failed to copy file: " + e.getMessage());
		}
	}
	
	public boolean areThumbnailFoldersCreated(String folderId) {
		Path lqThumbFolderPath = appConfig.getLowQualityThumbnailsPath().resolve(folderId);
		Path hqThumbFolderPath = appConfig.getHighQualityThumbnailsPath().resolve(folderId);
		
		return Files.isDirectory(lqThumbFolderPath) || Files.isDirectory(hqThumbFolderPath);
	}
	
	public byte[] getThumbnail(String folderId, String thumbnailName, ThumbnailQuality quality) throws IOException {
		Path filePath = null;
		switch (quality) {
			case LOW: filePath = appConfig.getLowQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName); break;
			case HIGH: filePath = appConfig.getHighQualityThumbnailsPath().resolve(folderId + "/" + thumbnailName); break;
		}
		return Files.readAllBytes(filePath);
	}
}
