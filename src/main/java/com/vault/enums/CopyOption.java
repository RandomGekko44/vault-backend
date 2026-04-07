package com.vault.enums;

//Indicates if a file or a folder is set to be added or replaced with other files
public enum CopyOption {
	ADD("ADD"),
	REPLACE("REPLACE");
	
	private final String option;
	
	CopyOption(String option) {
		this.option = option;
	}

	public String getOption() {
		return option;
	}
}
