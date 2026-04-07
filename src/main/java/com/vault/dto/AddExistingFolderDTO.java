package com.vault.dto;

import java.util.List;

import com.vault.models.FolderMetadata;
import com.vault.models.File;

public record AddExistingFolderDTO
(
	String name,
	FolderMetadata metadata,
	List<String> subfolders,
	List<File> files
) {}
