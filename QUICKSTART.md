# Quick Start Guide

## 5-Minute Setup & Test

### Prerequisites
- JDK 23+
- Maven (or IntelliJ with bundled Maven)
- Windows PowerShell (or any terminal)

### Step 1: Navigate to Project
```powershell
cd c:\Users\ammar\IdeaProjects\SDAProject
```

### Step 2: Compile
```powershell
mvn clean compile
```
**Expected Output**: `BUILD SUCCESS`

### Step 3: Run
```powershell
&"C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run
```
**Expected Result**: JavaFX window opens (no errors in console)

---

## First Test: Add & Report

1. **Click "Add Lab"**
   - ID: `LAB001`
   - Name: `Database Systems`
   - Building: `CS`
   - Room: `101`
   - Schedule Start: `2024-11-18 10:00`
   - Schedule End: `2024-11-18 12:00`
   - Click OK

2. **Click "Assign Instructor"**
   - Select "Assign Instructor" (radio button)
   - Name: `Dr. Smith`
   - Click OK

3. **Click "Enter TimeSheet"**
   - Select `LAB001`
   - Actual Start: `2024-11-18 10:05`
   - Actual End: `2024-11-18 11:50`
   - Click OK

4. **Click "Schedule Report"**
   - Year: `2024`
   - Week: `46`
   - See lab in report

5. **Click "Lab Semester Report"**
   - Select `LAB001`
   - See: Total Contact Hours: 1.75, Leaves: 0

6. **Click "Save"**
   - Data saved to `data/datastore.dat`

7. **Close & Reopen**
   - Close JavaFX window
   - Run `mvn javafx:run` again
   - Click "Load"
   - LAB001 reappears with all data

---

## Common Commands

### Build without Running
```powershell
mvn clean compile
```

### Run the App
```powershell
&"C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run
```

### Clean All
```powershell
mvn clean
```
(Removes `target/` and `data/datastore.dat`)

### Verify Compilation
```powershell
mvn compile -q
echo "Compiled successfully"
```

---

## File Locations

| File | Purpose |
|------|---------|
| `src/main/java/org/example/*.java` | Source code (14 files) |
| `data/datastore.dat` | Binary data file (created on save) |
| `pom.xml` | Maven configuration |
| `README.md` | Full feature documentation |
| `ARCHITECTURE.md` | Design & SOLID explanation |
| `SUMMARY.md` | Project overview |
| `QUICKSTART.md` | This file |

---

## Troubleshooting

### "Maven not found"
Use full path to IntelliJ's Maven:
```powershell
&"C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run
```

### "BUILD FAILURE"
1. Check JDK 23 installed: `java -version`
2. Clean and retry: `mvn clean compile`
3. Check for syntax errors in modified `.java` files

### JavaFX Window Won't Open
1. Ensure no other instance is running
2. Check console for errors (scroll up)
3. Try: `mvn clean compile` then run again

### Data Not Saving
1. Ensure you click "Save" button
2. Check `data/datastore.dat` exists after save
3. Verify no read-only permissions on `data/` folder

### Report Shows No Labs
1. First add labs using "Add Lab" button
2. Enter year/week matching your lab's schedule
3. For semester report, click "Lab Semester Report" and select the lab

---

## Quick Feature Check

- [ ] **Add Lab**: Click button, fill form, confirm in table
- [ ] **Assign Instructor**: Select lab, assign instructor, see name in table
- [ ] **Assign TA**: Select lab, assign TA, see multiple TAs in table
- [ ] **Enter TimeSheet**: Select lab, enter times, submit
- [ ] **Request Makeup**: Click button, select lab, enter time
- [ ] **Weekly Schedule Report**: Click button, enter year/week, see output
- [ ] **Weekly TimeSheet Report**: Click button, enter year/week, see sessions
- [ ] **Lab Semester Report**: Select lab, see hours/leaves/sessions
- [ ] **Save**: Click button, check `data/datastore.dat` created
- [ ] **Load**: Close and restart, click Load, data reappears

---

## Sample Data for Testing

```
Lab 1:
  ID: LAB001
  Name: Database Systems
  Building: CS, Room: 101
  Schedule: 2024-11-18 10:00 - 12:00
  Instructor: Dr. Smith

Lab 2:
  ID: LAB002
  Name: Web Development
  Building: CS, Room: 102
  Schedule: 2024-11-18 14:00 - 16:00
  Instructor: Dr. Jones

Instructor: Dr. Sarah Lee (ID: I-LAB003)
TA: John Doe, Jane Doe
```

---

## Tips & Tricks

1. **Use Load/Save frequently**: Don't lose work; save after each change
2. **Year/Week Format**: For year 2024, week 46 is mid-November; adjust based on your test dates
3. **Empty Timesheet = Leave**: Leave actual start/end blank to record absence
4. **Multiple TAs**: Click "Assign TA" multiple times for the same lab to add more TAs
5. **Makeup Approval**: Click "Approve Makeup" to mark request approved; then attendant can enter actual times

---

## What Happens When You Close?

The JavaFX window will close when you:
- Click the X button
- Press Ctrl+Q (or equivalent)

**Important**: Click "Save" before closing to persist data to `data/datastore.dat`.

---

## Support Files

For more details, read:
- **Usage & Features**: `README.md`
- **Architecture & Design**: `ARCHITECTURE.md`
- **Full Project Overview**: `SUMMARY.md`

---

**Last Updated**: November 16, 2025  
**Status**: Ready to Use ✅
