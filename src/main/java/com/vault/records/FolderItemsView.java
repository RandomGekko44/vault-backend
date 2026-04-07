package com.vault.records;

import java.util.List;

import com.vault.models.File;

public record FolderItemsView(List<String> subfolders, List<File> files) {
}
