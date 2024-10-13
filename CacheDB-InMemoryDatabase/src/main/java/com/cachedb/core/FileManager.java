package com.cachedb.core;

import java.io.*;
import java.nio.file.*;

public class FileManager {
    private static final String RESOURCES_DIR = "src/main/resources/generated";

    public void saveToFile(String key, CacheEntry entry) {
        Path directory = Paths.get(RESOURCES_DIR);
        try {
            Files.createDirectories(directory);
            Path file = directory.resolve(key + ".bin");
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
                oos.writeObject(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}