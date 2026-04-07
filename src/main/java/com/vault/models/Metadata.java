package com.vault.models;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Field;

public abstract class Metadata {
	private Long size;
	
	@Field("created_at")
	private LocalDateTime createdAt;
	
	@Field("modified_at")
	private LocalDateTime modifiedAt;
	
	public Long getSize() {
		return size;
	}
	
	public void setSize(Long size) {
		this.size = size;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}
	
	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	public void sumSize(Long sizeToAdd) {
		this.size += sizeToAdd;
	}
	
	public void subtractSize(Long sizeToSubtract) {
		this.size -= sizeToSubtract;
	}
}
