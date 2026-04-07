package com.vault.services;

import java.io.IOException;

import com.vault.enums.ThumbnailQuality;

public interface ThumbnailService {
	public byte[] getThumbnail(String folderId, String thumbnailName, ThumbnailQuality quality) throws IOException;
}
