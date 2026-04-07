package com.vault.dto;

import java.time.LocalDateTime;

public record FileMetadataRequestDTO(Long size, LocalDateTime createdAt, LocalDateTime modifiedAt) {

}
