package com.vault.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vault.models.FileMetadata;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
}
