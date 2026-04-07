package com.vault.dto;

import com.vault.models.FileMetadata;

public record FileRequestDTO(String name, FileMetadata metadata){}
