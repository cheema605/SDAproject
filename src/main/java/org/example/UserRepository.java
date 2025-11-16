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
            return createDefaultUsers();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return (List<User>) ois.readObject();
        }
    }
    
    /**
     * Creates default demo users for testing.
     */
    private List<User> createDefaultUsers() {
        List<User> users = new ArrayList<>();
        
        // Academic Officer
        users.add(new User("U-001", "officer", "pass123", "Dr. Alex Johnson", User.Role.ACADEMIC_OFFICER));
        
        // Attendants (one per building)
        users.add(new User("U-002", "attendant_cs", "pass123", "John Smith", User.Role.ATTENDANT, "CS Building"));
        users.add(new User("U-003", "attendant_eng", "pass123", "Sarah Davis", User.Role.ATTENDANT, "Engineering Wing"));
        
        // HOD
        users.add(new User("U-004", "hod", "pass123", "Prof. Michael Brown", User.Role.HOD));
        
        // Instructors
        users.add(new User("U-005", "instructor1", "pass123", "Dr. Smith", User.Role.INSTRUCTOR));
        users.add(new User("U-006", "instructor2", "pass123", "Dr. Jones", User.Role.INSTRUCTOR));
        
        // TAs
        users.add(new User("U-007", "ta1", "pass123", "John Doe", User.Role.TA));
        users.add(new User("U-008", "ta2", "pass123", "Jane Doe", User.Role.TA));
        
        return users;
    }
}
