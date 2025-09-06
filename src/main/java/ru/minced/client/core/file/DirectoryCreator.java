package ru.minced.client.core.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryCreator {
    public void createDirectories(File... files) {
        List<String> createdDirectories = new ArrayList<>();

        Arrays.stream(files)
                .filter(file -> !file.exists())
                .forEach(file -> {
                    if (file.mkdirs()) {
                        createdDirectories.add(file.getName());
                    }
                });
    }
}