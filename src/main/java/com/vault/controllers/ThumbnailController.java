package com.vault.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vault.enums.ThumbnailQuality;
import com.vault.services.ThumbnailService;

@CrossOrigin("*")
@RestController
@RequestMapping("api/folder")
public class ThumbnailController {
	@Autowired
	private ThumbnailService thumbnailService;
	
	@GetMapping(value = "/{folderId}/thumbnail/{thumbnailName}", produces = "image/webp")
	public @ResponseBody byte[] getThumbnail(@PathVariable String folderId, @PathVariable String thumbnailName, @RequestParam ThumbnailQuality quality) throws IOException {
		return thumbnailService.getThumbnail(folderId, thumbnailName, quality);
	}
}
