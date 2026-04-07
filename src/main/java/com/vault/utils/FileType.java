package com.vault.utils;

import org.springframework.stereotype.Component;

import com.vault.models.File;

@Component
public class FileType {
	public boolean isImage(File file) {		
		return (file.getMetadata().getType().startsWith("image/")) ? true : false;
	}
}
