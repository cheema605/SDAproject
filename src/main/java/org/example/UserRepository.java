package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * UserRepository handles persistence of User objects to binary files.
 * Single Responsibility: Manage user data I/O operations.
 */
public class UserRepository {

    private final Path filePath;

    public UserRepository(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves a list of users to disk.
     */
    public void saveUsers(List<User> users) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(users);
        }
    }

    /**
     * Loads users from disk. Returns default users if file doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public List<User> loadUsers() throws IOException, ClassNotFoundException {
        if (!Files.exists(filePath)) {
            List<User> defaults = createDefaultUsers();
            saveUsers(defaults);
            return defaults;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return (List<User>) ois.readObject();
        }
    }

    private List<User> createDefaultUsers() {
        List<User> users = new ArrayList<>();

        // Academic Officer
        users.add(new User("U-001", "officer", "pass123", "Dr. Kamran Akmal", User.Role.ACADEMIC_OFFICER));

        // Attendants (one per building)
        users.add(new User("U-002", "attendant_cs", "pass123", "Muhammad Ali", User.Role.ATTENDANT, "CS Building"));
        users.add(new User("U-003", "attendant_eng", "pass123", "Sana Mir", User.Role.ATTENDANT, "Engineering Wing"));

        // HOD
        users.add(new User("U-004", "hod", "pass123", "Prof. Javed Miandad", User.Role.HOD));

        // Instructors
        users.add(new User("U-005", "instructor1", "pass123", "Dr. Taimoor Pasha", User.Role.INSTRUCTOR));
        users.add(new User("U-006", "instructor2", "pass123", "Dr. Ayesha Siddiqui", User.Role.INSTRUCTOR));

        // TAs
        users.add(new User("U-007", "ta1", "pass123", "Hamza Khan", User.Role.TA));
        users.add(new User("U-008", "ta2", "pass123", "Fatima Batool", User.Role.TA));

        return users;
    }
}
