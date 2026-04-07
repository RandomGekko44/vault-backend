package com.vault.services.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vault.enums.ThumbnailQuality;
import com.vault.services.ThumbnailService;
import com.vault.utils.Thumbnails;

@Service
public class ThumbnailServiceImpl implements ThumbnailService{

	@Autowired
	private Thumbnails thumbnails;
	
	@Override
	public byte[] getThumbnail(String folderId, String thumbnailName, ThumbnailQuality quality) throws IOException {
		return thumbnails.getThumbnail(folderId, thumbnailName, quality);
	}
}
