package com.vault.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.vault.models.Folder;
import com.vault.records.FolderItemsView;
import com.vault.records.FolderView;
import com.vault.records.ParentFolderAndNameView;
import com.vault.records.RootFolderView;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
	Optional<Folder> findByMetadataPathBase64(String pathBase64);
	
	ParentFolderAndNameView findParentFolderAndNameById(String folderId);
	
	Optional<FolderItemsView> findFilesById(String folderId);
	
	List<RootFolderView> findByParentFolderIsNull();
	
	@Query("{ 'id' : ?0 }")
	FolderView findRootFolderById(String folderId);
	
	FolderView findByParentFolderAndName(String parentFolderId, String subFolderName);
}
