# Project Explanation

This document explains the structure and logic of the Lab Management System.

## Overview
The project is a JavaFX application for managing university labs. It allows different users (Instructors, TAs, etc.) to view and manage labs, schedules, and timesheets.

## Key Files

### 1. Main.java
-   **Purpose**: Starts the application.
-   **Logic**: It creates a `UserRepository` to load users and then shows the `LoginUI`.

### 2. UIController.java
-   **Purpose**: The "brain" of the UI. It handles all button clicks and data display.
-   **Key Method**: `getLabsForUser(User user, LabViewMode mode)`
    -   This method loops through all labs and decides which ones to show based on the user's role and the selected mode (All, Active Now, Today).
    -   It uses simple `for` loops and `if` statements to check conditions.

### 3. LabRepository.java
-   **Purpose**: Handles saving and loading data to a file.
-   **Logic**: It uses Java's `ObjectOutputStream` and `ObjectInputStream` to write/read the entire `DataStore` object to/from a file.

### 4. DataStore.java
-   **Purpose**: A simple container for all data (Labs, Instructors, TAs, Requests).
-   **Logic**: It just has lists and getters.

### 5. POJOs (Plain Old Java Objects)
-   **User.java**: Represents a user. Has a `Role` enum.
-   **Lab.java**: Represents a lab. Contains `Schedule`, `Venue`, `Instructor`, and `TimeSheet` list.
-   **Schedule.java**: Stores start and end times.

## How it Works
1.  **Startup**: `Main` loads users.
2.  **Login**: User logs in.
3.  **Dashboard**: `UIController` is initialized. It loads labs from `LabRepository`.
4.  **Filtering**: When a user logs in, `getLabsForUser` filters the labs they can see.
    -   **Instructor**: Sees their own labs.
    -   **TA**: Sees labs they are assigned to.
    -   **Attendant**: Sees labs in their building.
    -   **Academic Officer/HOD**: Sees all labs.
5.  **Actions**: Buttons like "Add Lab" or "Assign Staff" open dialogs (via `DialogFactory`) and then update the `labs` list.

## Note
This code is written in a simple style to be easy to understand. Complex Java features like Streams have been replaced with standard loops.
