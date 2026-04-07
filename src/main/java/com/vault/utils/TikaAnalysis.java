package com.vault.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

@Component
public class TikaAnalysis {
	private final Tika tika = new Tika();
	
	public String detectFileType(InputStream stream) throws IOException {
		Metadata metadata = new Metadata();
		TikaInputStream tikaStream = TikaInputStream.get(stream);
		
		return tika.detect(tikaStream, metadata);
	}
	
	public boolean isImage(Path filePath) throws IOException {
		InputStream stream = new FileInputStream(filePath.toFile());
		return detectFileType(stream).startsWith("image/");
	}
	
	public boolean isImage(InputStream stream) throws IOException {
		return detectFileType(stream).startsWith("image/");
	}
}
