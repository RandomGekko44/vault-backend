package com.vault.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vault.models.FileMetadata;
import com.vault.repositories.FileMetadataRepository;
import com.vault.services.FileMetadataService;

@Service
public class FileMetadataServiceImpl implements FileMetadataService {
	
	@Autowired
	private FileMetadataRepository fileMetadataRepository;
	
	@Override
	public void addFileMetadata(FileMetadata fileMetadata) {
		fileMetadataRepository.insert(fileMetadata);
	}
}
