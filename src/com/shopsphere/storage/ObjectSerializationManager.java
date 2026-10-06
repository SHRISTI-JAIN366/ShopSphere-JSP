package com.shopsphere.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages object persistence via Java Native Object Serialization (.ser / .dat).
 * Demonstrates ObjectOutputStream and ObjectInputStream.
 */
public class ObjectSerializationManager {

    private static final String DATA_DIR = "data";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    public static <T extends Serializable> boolean saveList(List<T> list, String filename) {
        String filepath = DATA_DIR + File.separator + filename;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(list);
            return true;
        } catch (IOException e) {
            System.err.println("Error serializing to " + filepath + ": " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> loadList(String filename) {
        String filepath = DATA_DIR + File.separator + filename;
        File file = new File(filepath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error deserializing from " + filepath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
