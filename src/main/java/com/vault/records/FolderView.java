package com.vault.records;

import java.util.List;

public record FolderView(String id, String name, List<String> subfolders, List<FileView> files) {
	
	public record FileView(String id, String name, FileMetadataView metadata) {
		
		public record FileMetadataView(String type) {
		}
	}
}
