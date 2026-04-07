package com.vault.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import com.vault.models.Folder;
import com.vault.configs.AppConfig;
import com.vault.enums.ImageExtensions;
import com.vault.models.File;

/**
 * Compresses images to the WEBP format and generates thumbnails.
 */
@Component
public class ThumbnailGenerator {
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private Thumbnails thumbnails;
	
	@Autowired
	private FileType fileType;
	
	private final WebpWriter writer = new WebpWriter();
	//Allows for multithreading
	private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private final String thumbnailFileExtension = ".webp";
	
	public void createThumbnail(String folderId, java.io.File file, String fileId) {
		compressionSetUp(folderId, file, fileId);
	}
	
	public void createThumbnails(Folder folder) throws IOException {
		String sourceFolder = folder.getMetadata().getPath();
		
		if (!thumbnails.areThumbnailFoldersCreated(folder.getId())) {
			thumbnails.createThumbnailFolders(folder.getId());
		}
		
		for (File file : folder.getFiles()) {
			if (fileType.isImage(file)) {
				java.io.File storedFile = Paths.get(sourceFolder, file.getName()).toFile();
				
				executor.submit(() -> {
					compressionSetUp(folder.getId(), storedFile, file.getId());
				});
			}
		}
	}
	
	private void compressionSetUp(String folderId, java.io.File file, String outputFileName) {
      try {
    	  ImmutableImage image = ImmutableImage.loader().fromFile(file);
    	  String partialThumbFilePath = Paths.get(folderId, outputFileName.concat(thumbnailFileExtension)).toString();
    	  
    	  // Low quality
    	  startCompression(
        	  image.scaleToWidth(500),
        	  writer,
        	  appConfig.getLowQualityThumbnailsPath().resolve(partialThumbFilePath)
          );

          // High quality
    	  startCompression(
              image.scaleToWidth(1500),
              writer,
              appConfig.getHighQualityThumbnailsPath().resolve(partialThumbFilePath)
          );
      }
      catch (IOException e) {
    	  System.err.println("Could not load image: " + file.getPath().toString());
    	  e.printStackTrace();
      }
	}
	
	private void startCompression(ImmutableImage image, WebpWriter writer, Path output) {
		try {
			image.output(writer, output.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
