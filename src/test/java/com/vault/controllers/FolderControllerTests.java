package com.vault.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.SpringVersion;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vault.dto.FolderDTO;
import com.vault.models.FolderMetadata;
import com.vault.services.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FolderController.class)
public class FolderControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private FolderService folderService;
	
	@Nested
	class AddFolderTests {
		@Test
		@DisplayName("Succesfully adds a new folder")
		void shouldAddFolderSuccesfully() throws Exception {
			FolderMetadata metadata = new FolderMetadata();
			metadata.setSize(0L);
			metadata.setCreatedAt(LocalDateTime.now());
			metadata.setModifiedAt(metadata.getCreatedAt());
			metadata.setFileAmount((long) 0);
			metadata.setPath("/home/educc/Documents/Vault-Project/test_folder");
			metadata.setPathBase64(Base64.getEncoder().encodeToString(
				metadata.getPath().getBytes()
			));
			
			FolderDTO folderDTO = new FolderDTO("test_folder", metadata);
			
			mockMvc.perform(post("http://localhost:8080/api/folder/add")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(folderDTO)))
					.andExpect(status().isOk());
		}
	}
}
