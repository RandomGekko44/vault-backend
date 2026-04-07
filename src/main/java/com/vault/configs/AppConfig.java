package com.vault.configs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
	
	@Value("${thumbnails.folder.low.quality}")
	private String lowQualityThumbnailsPath;
	
	@Value("${thumbnails.folder.high.quality}")
	private String highQualityThumbnailsPath;
	
	private final String thumbnailFileExtension = ".webp";

	public Path getLowQualityThumbnailsPath() {
		return Paths.get(lowQualityThumbnailsPath);
	}

	public Path getHighQualityThumbnailsPath() {
		return Paths.get(highQualityThumbnailsPath);
	}

	public String getThumbnailFileExtension() {
		return thumbnailFileExtension;
	}
}
