package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// class to handle saving and loading data
public class LabRepository {
    private Path storagePath;

    // constructor
    public LabRepository(Path storagePath) {
        this.storagePath = storagePath;
    }

    // save data to file
    public void save(DataStore ds) throws IOException {
        Files.createDirectories(storagePath.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storagePath.toFile()))) {
            oos.writeObject(ds);
        }
    }

    // load data from file
    public DataStore load() throws IOException, ClassNotFoundException {
        if (!Files.exists(storagePath))
            return new DataStore();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storagePath.toFile()))) {
            return (DataStore) ois.readObject();
        }
    }
}
