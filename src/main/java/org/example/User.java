package org.example;

import java.io.Serializable;

// User class
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        ACADEMIC_OFFICER("Academic Officer"),
        ATTENDANT("Lab Attendant"),
        HOD("Head of Department"),
        INSTRUCTOR("Lab Instructor"),
        TA("Teaching Assistant");

        private String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String id;
    private String username;
    private String password;
    private String name;
    private Role role;
    private String building; // for attendants

    // constructor
    public User(String id, String username, String password, String name, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // constructor with building
    public User(String id, String username, String password, String name, Role role, String building) {
        this(id, username, password, name, role);
        this.building = building;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public String toString() {
        return name + " (" + role.getDisplayName() + ")";
    }
}
