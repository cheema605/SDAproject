# Labs Management System

A JavaFX-based system to manage information about labs in a university, storing data about labs, sections, timings, instructors, TAs, venues, and both scheduled and actual timesheets.

## Architecture & SOLID Principles

### Design Overview
The system follows **SOLID principles** with a clean, layered architecture:

- **Models** (`Lab`, `Venue`, `Instructor`, `TA`, `Schedule`, `TimeSheet`, `MakeupLabRequest`, `DataStore`): Serializable domain classes representing core business entities. Each model has a single responsibility.
  
- **Repository** (`LabRepository`): Abstracts binary file I/O. All persistence (serialization) goes through this single interface, following the **Repository Pattern** and **Single Responsibility Principle**. Persists entire `DataStore` to `data/datastore.dat`.

- **Services** (`AcademicOfficer`, `Attendant`, `HeadOfDepartment`): Encapsulate workflows and business logic:
  - **AcademicOfficer**: Creates labs, assigns instructors/TAs, sets schedules. (Single Responsibility: lab setup)
  - **Attendant**: Enters timesheets and approves makeup requests. (Single Responsibility: attendance recording)
  - **HeadOfDepartment**: Generates all three required reports. (Single Responsibility: reporting)
  
- **UI** (`Main.java`): JavaFX application that orchestrates user interactions and delegates to services.

### SOLID Mapping
1. **Single Responsibility**: Each class has one reason to change (e.g., `LabRepository` only handles persistence; `HeadOfDepartment` only generates reports).
2. **Open/Closed**: New report types can be added to `HeadOfDepartment` without modifying existing code.
3. **Liskov Substitution**: `Instructor` and `TA` both extend `Person`, can be used interchangeably in person-related operations.
4. **Interface Segregation**: Services expose only the methods clients need (no bloated interfaces).
5. **Dependency Inversion**: Services depend on `DataStore` abstraction, not concrete UI or file paths.

---

## Features Implemented

### 1. **Lab Management**
- Add new labs with ID, name, venue (building/room), and expected schedule (start/end time).
- Assign instructors and multiple TAs to labs.
- Edit and manage lab assignments.

### 2. **TimeSheet Recording**
- Attendants enter actual start/end times for lab sessions.
- Mark absences by leaving actual times empty (recorded as "leave").
- Sessions are persisted as a list within each lab; compute contact hours and absence count dynamically.

### 3. **Makeup Lab Requests**
- Instructors request makeup labs with proposed schedule.
- Attendants approve/reject requests (marked in `MakeupLabRequest.approved` flag).
- Approved makeups can be entered as sessions when actually conducted.

### 4. **Three Required Reports**

#### a. **Weekly Schedule Report**
- Shows all labs scheduled for a given year and week.
- Displays lab ID, name, venue, instructor, and expected schedule.
- Queries labs by year/week using `LocalDateTime.get(WeekFields.of(Locale.US).weekOfYear())`.

#### b. **Weekly TimeSheet Report**
- Aggregates all actual sessions (timesheets) recorded for a given year/week.
- Groups by lab and displays actual start/end times.
- Shows which labs had activity that week.

#### c. **Lab Semester TimeSheet Report**
- Comprehensive report for a single lab's entire semester.
- Lists all sessions (actual timesheets) with start/end times.
- Calculates:
  - **Total Contact Hours**: Sum of (actualEnd - actualStart) for all completed sessions.
  - **Leaves (Absences)**: Count of sessions where actualStart/actualEnd is null.
  - **Total Sessions**: Count of all sessions.
- Includes approved makeup requests (shown separately).

### 5. **Binary Serialization**
- All data (`DataStore` containing labs, instructors, TAs, requests) is serialized to `data/datastore.dat`.
- No SQL database; only binary file I/O via Java's `ObjectInputStream`/`ObjectOutputStream`.
- Load/Save buttons sync in-memory UI state with disk.

---

## Project Structure

```
src/main/java/org/example/
  ├── Main.java                    # JavaFX application (UI)
  ├── Lab.java                     # Lab entity with sessions list
  ├── Venue.java                   # Venue (building/room)
  ├── Schedule.java                # Expected schedule (start/end)
  ├── TimeSheet.java               # Actual session times
  ├── Person.java                  # Abstract base class for instructors/TAs
  ├── Instructor.java              # Instructor (extends Person)
  ├── TA.java                       # TA (extends Person)
  ├── MakeupLabRequest.java         # Makeup request entity
  ├── DataStore.java                # Container for all domain lists
  ├── LabRepository.java            # Persistence (binary file I/O)
  ├── AcademicOfficer.java         # Service: lab setup & assignments
  ├── Attendant.java               # Service: timesheet & makeup approval
  └── HeadOfDepartment.java        # Service: report generation

data/
  └── datastore.dat               # Binary file (created on first save)

pom.xml                            # Maven config with JavaFX dependencies
```

---

## Building & Running

### Prerequisites
- JDK 23 or higher
- JavaFX 25

### Setup (First Time Only)

The project includes **Maven Wrapper** (mvnw) which downloads and manages Maven automatically. No need to install Maven separately.

**Windows:**
```cmd
setup.cmd
```

**Linux/Mac:**
```bash
bash setup.sh
```

These scripts will:
1. Auto-detect your Java installation
2. Configure the JAVA_HOME environment variable
3. Verify Maven wrapper is ready

### Build

```bash
# Windows:
.\mvnw.cmd clean compile

# Linux/Mac:
./mvnw clean compile
```

### Run the UI

```bash
# Windows:
.\mvnw.cmd javafx:run

# Linux/Mac:
./mvnw javafx:run
```

### Run Tests

```bash
# Windows:
.\mvnw.cmd test

# Linux/Mac:
./mvnw test
```

### Manual Maven Commands

You can run any Maven command with the wrapper:
```bash
# Windows:
.\mvnw.cmd clean install
.\mvnw.cmd package

# Linux/Mac:
./mvnw clean install
./mvnw package
```

The JavaFX window will open with a table of labs and buttons for:
- **Load** / **Save**: Sync with `data/datastore.dat`
- **Add Lab**: Create new lab with venue and schedule
- **Assign Instructor** / **Assign TA**: Add personnel to labs
- **Enter TimeSheet**: Record actual session times
- **Request Makeup** / **Approve Makeup**: Manage makeup workflows
- **Schedule Report** / **Weekly TimeSheet** / **Lab Semester Report**: Generate reports

---

## Usage Example

1. **Add a Lab:**
   - Click "Add Lab"
   - Enter: ID = "LAB001", Name = "Database Systems", Building = "CS", Room = "101"
   - Schedule Start = "2024-11-18 10:00", Schedule End = "2024-11-18 12:00"
   - Click OK

2. **Assign Instructor:**
   - Select "LAB001" from table
   - Click "Assign Instructor"
   - Select "Instructor"
   - Enter name: "Dr. Smith"
   - Click OK

3. **Assign TA:**
   - Select "LAB001"
   - Click "Assign TA"
   - Select "TA"
   - Enter name: "John Doe"
   - Click OK

4. **Enter TimeSheet:**
   - Select "LAB001"
   - Click "Enter TimeSheet"
   - Actual Start: "2024-11-18 10:05"
   - Actual End: "2024-11-18 11:50"
   - Click OK (records ~1.75 hours)

5. **Request Makeup:**
   - Select "LAB001"
   - Click "Request Makeup"
   - Enter makeup start/end times
   - Click OK (creates request; attendant can approve later)

6. **Generate Reports:**
   - **Schedule Report**: Shows all labs scheduled for a given year/week
   - **Weekly TimeSheet**: Shows recorded sessions for that week
   - **Lab Semester Report**: Shows total hours, absences, and all sessions for LAB001

7. **Save & Load:**
   - Click "Save" to persist all data to `data/datastore.dat`
   - Click "Load" to reload from disk

---

## Design Decisions

### Why Binary Serialization?
- Direct, simple, no schema management
- Fast I/O for small datasets
- Meets requirement: "use binary files, no DBMS"

### Why Service Classes?
- Decouples UI from business logic
- Testable, reusable workflows
- Follows Single Responsibility Principle
- Easy to extend (e.g., add email notifications, audit logs)

### Why Multiple TAs per Lab?
- Realistic: labs often have multiple TAs
- Stored as `List<TA>` in `Lab`; supports `addTA()` / `removeTA()`

### Lab Sessions as a List
- Supports semester-long tracking of all activities
- Compute contact hours and leaves dynamically
- Simple: no separate session/attendance table needed

---

## Testing & Validation

To manually test:
1. Add 2–3 labs with different venues and schedules
2. Assign instructors and TAs
3. Enter timesheets (mix of present and absent)
4. Request and approve a makeup
5. Generate each report and verify:
   - Weekly Schedule shows correct labs and times
   - Weekly TimeSheet shows actual session records
   - Lab Semester Report shows correct totals and makeup notes
6. Save, restart the app, Load, and verify data persists

---

## Future Enhancements (Optional)

- CSV export for reports
- Multiple attendants per building (with login)
- Email notifications for makeup approvals
- Semester configuration (start/end dates)
- Audit trail of changes
- Query/filter labs by instructor or building
- Web UI (Spring Boot + Thymeleaf)

---

## Compliance Notes

- **Binary Storage**: ✅ Uses `ObjectInputStream`/`ObjectOutputStream` to `data/datastore.dat`
- **No DBMS**: ✅ Pure Java serialization
- **All Required Features**: ✅ Lab mgmt, schedule, timesheet, makeup requests, three reports
- **SOLID Principles**: ✅ Single responsibility per class; service layer abstraction; extensible design
- **Serializable Models**: ✅ All entities implement `Serializable` with `serialVersionUID`

---

**Version**: 1.0  
**Last Updated**: November 16, 2025  
**Author**: SDAProject Team
