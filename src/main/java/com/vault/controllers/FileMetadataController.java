package com.vault.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vault.models.FileMetadata;
import com.vault.services.FileMetadataService;

@RestController
@RequestMapping("api/file/metadata")
public class FileMetadataController {
	
	@Autowired
	private FileMetadataService fileMetadataService;
	
	@PostMapping("/add")
	public void addFileMetadata(@RequestBody FileMetadata fileMetadata) {
		fileMetadataService.addFileMetadata(fileMetadata);
	}
}
