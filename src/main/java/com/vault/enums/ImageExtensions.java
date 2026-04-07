package com.vault.enums;

public enum ImageExtensions {
	JPG("jpg"),
	JPEG("jpeg"),
	PNG("png");
	
	private final String extension;

	private ImageExtensions(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}
	
	public static boolean contains(String extension) {
		if (extension == null) return false;
		
		extension = extension.toLowerCase();
		for (ImageExtensions e : values()) {
			if (e.extension.equals(extension)) {
				return true;
			}
		}
		return false;
	}
}
