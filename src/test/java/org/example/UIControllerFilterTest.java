package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UIController.getLabsForUser filtering logic.
 * Tests: role-based privacy, active-state detection (ALL, ACTIVE_NOW, TODAY modes).
 */
public class UIControllerFilterTest {

    private UIController uiController;
    private DataStore dataStore;

    @BeforeEach
    public void setUp() {
        // Create test data first
        dataStore = new DataStore();
        
        // Create test labs
        LocalDateTime now = LocalDateTime.now();
        
        // Lab 1: Currently active, taught by Ahmed Khan, TA is Hasan, in Building A
        Lab lab1 = new Lab("1", "CS101 - Introduction to CS");
        Instructor inst1 = new Instructor("1", "Dr. Ahmed Khan");
        lab1.setInstructor(inst1);
        TA ta1 = new TA("1", "Hasan Taj");
        lab1.getTas().add(ta1);
        Venue venue1 = new Venue("Building A", "Room 101");
        lab1.setVenue(venue1);
        Schedule sched1 = new Schedule();
        sched1.setExpectedStart(now.minusHours(1)); // started 1 hour ago
        sched1.setExpectedEnd(now.plusHours(1));   // ends in 1 hour
        lab1.setSchedule(sched1);
        
        // Lab 2: Not currently active (scheduled for tomorrow), taught by Ali Khan, TA is Usman, in Building B
        Lab lab2 = new Lab("2", "CS102 - Data Structures");
        Instructor inst2 = new Instructor("2", "Ali Khan");
        lab2.setInstructor(inst2);
        TA ta2 = new TA("2", "Usman R");
        lab2.getTas().add(ta2);
        Venue venue2 = new Venue("Building B", "Room 202");
        lab2.setVenue(venue2);
        Schedule sched2 = new Schedule();
        sched2.setExpectedStart(now.plusDays(1).minusHours(2));
        sched2.setExpectedEnd(now.plusDays(1).plusHours(2));
        lab2.setSchedule(sched2);
        
        // Lab 3: Scheduled for today but earlier time (not active now), taught by Ahmed Khan, in Building A
        Lab lab3 = new Lab("3", "CS103 - Algorithms");
        Instructor inst3 = new Instructor("3", "Dr. Ahmed Khan");
        lab3.setInstructor(inst3);
        Venue venue3 = new Venue("Building A", "Room 103");
        lab3.setVenue(venue3);
        Schedule sched3 = new Schedule();
        sched3.setExpectedStart(now.minusHours(5)); // 5 hours ago
        sched3.setExpectedEnd(now.minusHours(3));  // ended 3 hours ago
        lab3.setSchedule(sched3);
        
        // Lab 4: In Building B, not taught by any instructor, with TimeSheet ongoing
        Lab lab4 = new Lab("4", "CS104 - Databases");
        TA ta3 = new TA("3", "Some Other TA");
        lab4.getTas().add(ta3);
        Venue venue4 = new Venue("Building B", "Room 204");
        lab4.setVenue(venue4);
        Schedule sched4 = new Schedule();
        sched4.setExpectedStart(now.plusDays(2));
        sched4.setExpectedEnd(now.plusDays(2).plusHours(2));
        lab4.setSchedule(sched4);
        TimeSheet activeSession = new TimeSheet();
        activeSession.setActualStart(now.minusMinutes(15)); // started 15 minutes ago
        activeSession.setActualEnd(now.plusHours(1));
        lab4.getSessions().add(activeSession);
        
        dataStore.getLabs().addAll(List.of(lab1, lab2, lab3, lab4));
        
        // Create a LabRepository that loads our test data
        LabRepository mockRepository = new LabRepository(java.nio.file.Path.of("data", "test_datastore.dat")) {
            @Override
            public DataStore load() {
                return dataStore;
            }
        };
        
        // Create UIController - it will use our mock repository's data
        uiController = new UIController(mockRepository);
    }

    // ===================== ACTIVE_NOW Mode Tests =====================

    @Test
    public void testInstructorSeesOwnLabsActiveNow() {
        User instructor = new User("1", "al.khan", "pass", "Dr. Ahmed Khan", User.Role.INSTRUCTOR, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(instructor, UIController.LabViewMode.ACTIVE_NOW);
        
        // Ahmed Khan should see lab1 (currently active) but not lab3 (ended 3 hours ago)
        assertEquals(1, result.size(), "Instructor should see 1 active lab (CS101)");
        assertEquals("CS101 - Introduction to CS", result.get(0).getName());
    }

    @Test
    public void testTASeesAssignedLabsActiveNow() {
        User ta = new User("3", "hasan.taj", "pass", "Hasan Taj", User.Role.TA, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(ta, UIController.LabViewMode.ACTIVE_NOW);
        
        // Hasan should see lab1 (currently active, assigned to him)
        assertEquals(1, result.size(), "TA should see 1 active assigned lab");
        assertEquals("CS101 - Introduction to CS", result.get(0).getName());
    }

    @Test
    public void testAttendantSeesBuildingLabsActiveNow() {
        User attendant = new User("5", "fatima.s", "pass", "Fatima S", User.Role.ATTENDANT, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(attendant, UIController.LabViewMode.ACTIVE_NOW);
        
        // Fatima (Building A attendant) should see lab1 (active, in Building A)
        // Lab4 has TimeSheet session active but it's in Building B, so she shouldn't see it
        assertEquals(1, result.size(), "Attendant in Building A should see 1 active lab");
        assertEquals("CS101 - Introduction to CS", result.get(0).getName());
    }

    @Test
    public void testAcademicOfficerSeesAllActiveNow() {
        User academicOfficer = new User("7", "officer", "pass", "Officer One", User.Role.ACADEMIC_OFFICER, "Admin");
        ObservableList<Lab> result = uiController.getLabsForUser(academicOfficer, UIController.LabViewMode.ACTIVE_NOW);
        
        // AO should see lab1 (active schedule) and lab4 (active TimeSheet)
        assertEquals(2, result.size(), "Academic Officer should see 2 active labs");
        List<String> names = result.stream().map(Lab::getName).toList();
        assertTrue(names.contains("CS101 - Introduction to CS"));
        assertTrue(names.contains("CS104 - Databases"));
    }

    // ===================== ALL Mode Tests =====================

    @Test
    public void testInstructorSeesAllOwnLabs() {
        User instructor = new User("1", "al.khan", "pass", "Dr. Ahmed Khan", User.Role.INSTRUCTOR, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(instructor, UIController.LabViewMode.ALL);
        
        // Ahmed Khan should see lab1 (CS101) and lab3 (CS103)
        assertEquals(2, result.size(), "Instructor should see all 2 of his labs");
        List<String> names = result.stream().map(Lab::getName).toList();
        assertTrue(names.contains("CS101 - Introduction to CS"));
        assertTrue(names.contains("CS103 - Algorithms"));
    }

    @Test
    public void testTASeesAllAssignedLabs() {
        User ta = new User("3", "hasan.taj", "pass", "Hasan Taj", User.Role.TA, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(ta, UIController.LabViewMode.ALL);
        
        // Hasan should see lab1 (assigned to him)
        assertEquals(1, result.size(), "TA should see all 1 of his assigned labs");
        assertEquals("CS101 - Introduction to CS", result.get(0).getName());
    }

    @Test
    public void testAttendantSeesBuildingLabsAll() {
        User attendant = new User("5", "fatima.s", "pass", "Fatima S", User.Role.ATTENDANT, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(attendant, UIController.LabViewMode.ALL);
        
        // Fatima (Building A) should see lab1 and lab3 (both in Building A)
        assertEquals(2, result.size(), "Attendant should see all labs in their building");
        List<String> names = result.stream().map(Lab::getName).toList();
        assertTrue(names.contains("CS101 - Introduction to CS"));
        assertTrue(names.contains("CS103 - Algorithms"));
    }

    @Test
    public void testAcademicOfficerSeesAllLabs() {
        User academicOfficer = new User("7", "officer", "pass", "Officer One", User.Role.ACADEMIC_OFFICER, "Admin");
        ObservableList<Lab> result = uiController.getLabsForUser(academicOfficer, UIController.LabViewMode.ALL);
        
        // AO should see all 4 labs
        assertEquals(4, result.size(), "Academic Officer should see all labs");
    }

    // ===================== TODAY Mode Tests =====================

    @Test
    public void testInstructorSeesTodayLabs() {
        User instructor = new User("1", "al.khan", "pass", "Dr. Ahmed Khan", User.Role.INSTRUCTOR, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(instructor, UIController.LabViewMode.TODAY);
        
        // Ahmed Khan should see lab1 and lab3 (both today, even though lab3 is past)
        assertEquals(2, result.size(), "Instructor should see both today labs");
        List<String> names = result.stream().map(Lab::getName).toList();
        assertTrue(names.contains("CS101 - Introduction to CS"));
        assertTrue(names.contains("CS103 - Algorithms"));
    }

    @Test
    public void testAttendantSeesTodayBuildingLabs() {
        User attendant = new User("5", "fatima.s", "pass", "Fatima S", User.Role.ATTENDANT, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(attendant, UIController.LabViewMode.TODAY);
        
        // Fatima should see lab1 and lab3 (both today in Building A)
        assertEquals(2, result.size(), "Attendant should see today labs in their building");
        List<String> names = result.stream().map(Lab::getName).toList();
        assertTrue(names.contains("CS101 - Introduction to CS"));
        assertTrue(names.contains("CS103 - Algorithms"));
    }

    // ===================== Privacy Tests =====================

    @Test
    public void testInstructorCannotSeeOtherInstructorLabs() {
        User instructor1 = new User("1", "al.khan", "pass", "Dr. Ahmed Khan", User.Role.INSTRUCTOR, "Building A");
        User instructor2 = new User("2", "ali.khan", "pass", "Ali Khan", User.Role.INSTRUCTOR, "Building B");
        
        ObservableList<Lab> result = uiController.getLabsForUser(instructor2, UIController.LabViewMode.ALL);
        
        // Instructor2 (Ali Khan) should see only his labs, not Ahmed Khan's labs
        assertEquals(1, result.size(), "Instructor2 should not see Instructor1's labs");
        assertEquals("CS102 - Data Structures", result.get(0).getName());
    }

    @Test
    public void testTACannotSeeLaboratoryOutsideAssignment() {
        User ta = new User("3", "hasan.taj", "pass", "Hasan Taj", User.Role.TA, "Building A");
        ObservableList<Lab> result = uiController.getLabsForUser(ta, UIController.LabViewMode.ALL);
        
        // Hasan is only assigned to lab1, not to lab2, lab3, or lab4
        assertEquals(1, result.size(), "TA should only see assigned labs");
        assertEquals("CS101 - Introduction to CS", result.get(0).getName());
    }

    @Test
    public void testAttendantCannotSeeLaboratoryOutsideBuilding() {
        User attendantA = new User("5", "fatima.s", "pass", "Fatima S", User.Role.ATTENDANT, "Building A");
        User attendantB = new User("6", "ayesha.a", "pass", "Ayesha A", User.Role.ATTENDANT, "Building B");
        
        ObservableList<Lab> resultA = uiController.getLabsForUser(attendantA, UIController.LabViewMode.ALL);
        ObservableList<Lab> resultB = uiController.getLabsForUser(attendantB, UIController.LabViewMode.ALL);
        
        // Fatima (Building A) should only see Building A labs
        assertEquals(2, resultA.size());
        for (Lab lab : resultA) {
            assertEquals("Building A", lab.getVenue().getBuilding());
        }
        
        // Ayesha (Building B) should only see Building B labs
        assertEquals(2, resultB.size());
        for (Lab lab : resultB) {
            assertEquals("Building B", lab.getVenue().getBuilding());
        }
    }

    @Test
    public void testHODSeesAllLabs() {
        User hod = new User("8", "hod", "pass", "HOD One", User.Role.HOD, "Admin");
        ObservableList<Lab> result = uiController.getLabsForUser(hod, UIController.LabViewMode.ALL);
        
        // HOD should see all labs
        assertEquals(4, result.size(), "HOD should see all labs");
    }
}

