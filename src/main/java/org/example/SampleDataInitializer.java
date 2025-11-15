package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleDataInitializer {
    
    public static DataStore generateSampleData() {
        DataStore ds = new DataStore();
        
        // Create instructors
        Instructor dr_smith = new Instructor("I-001", "Dr. Smith");
        Instructor dr_jones = new Instructor("I-002", "Dr. Jones");
        Instructor dr_lee = new Instructor("I-003", "Dr. Sarah Lee");
        
        ds.getInstructors().add(dr_smith);
        ds.getInstructors().add(dr_jones);
        ds.getInstructors().add(dr_lee);
        
        // Create TAs
        TA john_doe = new TA("TA-001", "John Doe");
        TA jane_doe = new TA("TA-002", "Jane Doe");
        TA mike_wilson = new TA("TA-003", "Mike Wilson");
        TA sarah_adams = new TA("TA-004", "Sarah Adams");
        
        ds.getTas().add(john_doe);
        ds.getTas().add(jane_doe);
        ds.getTas().add(mike_wilson);
        ds.getTas().add(sarah_adams);
        
        // Lab 1: Database Systems
        Lab lab1 = new Lab("LAB001", "Database Systems");
        lab1.setVenue(new Venue("CS Building", "Room 101"));
        lab1.setSchedule(new Schedule(
            LocalDateTime.of(2024, 11, 18, 10, 0),
            LocalDateTime.of(2024, 11, 18, 12, 0)
        ));
        lab1.setInstructor(dr_smith);
        lab1.addTA(john_doe);
        lab1.addTA(jane_doe);
        
        // Add sample sessions for Lab 1
        lab1.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 4, 10, 5),
            LocalDateTime.of(2024, 11, 4, 11, 55)
        ));
        lab1.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 11, 10, 0),
            LocalDateTime.of(2024, 11, 11, 12, 0)
        ));
        lab1.addSession(new TimeSheet(null, null)); // Absence/leave
        lab1.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 25, 10, 10),
            LocalDateTime.of(2024, 11, 25, 11, 50)
        ));
        
        ds.getLabs().add(lab1);
        
        // Lab 2: Web Development
        Lab lab2 = new Lab("LAB002", "Web Development");
        lab2.setVenue(new Venue("CS Building", "Room 102"));
        lab2.setSchedule(new Schedule(
            LocalDateTime.of(2024, 11, 18, 14, 0),
            LocalDateTime.of(2024, 11, 18, 16, 0)
        ));
        lab2.setInstructor(dr_jones);
        lab2.addTA(mike_wilson);
        
        // Add sample sessions for Lab 2
        lab2.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 4, 14, 0),
            LocalDateTime.of(2024, 11, 4, 15, 45)
        ));
        lab2.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 11, 14, 5),
            LocalDateTime.of(2024, 11, 11, 16, 0)
        ));
        lab2.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 18, 14, 0),
            LocalDateTime.of(2024, 11, 18, 16, 0)
        ));
        
        ds.getLabs().add(lab2);
        
        // Lab 3: Data Structures
        Lab lab3 = new Lab("LAB003", "Data Structures");
        lab3.setVenue(new Venue("Engineering Wing", "Room 301"));
        lab3.setSchedule(new Schedule(
            LocalDateTime.of(2024, 11, 19, 10, 0),
            LocalDateTime.of(2024, 11, 19, 11, 30)
        ));
        lab3.setInstructor(dr_lee);
        lab3.addTA(sarah_adams);
        lab3.addTA(john_doe);
        
        // Add sample sessions for Lab 3
        lab3.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 5, 10, 0),
            LocalDateTime.of(2024, 11, 5, 11, 30)
        ));
        lab3.addSession(new TimeSheet(null, null)); // Absence
        lab3.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 19, 10, 5),
            LocalDateTime.of(2024, 11, 19, 11, 35)
        ));
        lab3.addSession(new TimeSheet(
            LocalDateTime.of(2024, 11, 26, 10, 0),
            LocalDateTime.of(2024, 11, 26, 11, 30)
        ));
        
        ds.getLabs().add(lab3);
        
        // Add a sample makeup request (approved)
        MakeupLabRequest makeup1 = new MakeupLabRequest(
            "MR-001",
            "LAB001",
            "I-001",
            new Schedule(
                LocalDateTime.of(2024, 12, 2, 10, 0),
                LocalDateTime.of(2024, 12, 2, 12, 0)
            )
        );
        makeup1.setApproved(true);
        ds.getRequests().add(makeup1);
        
        // Add another makeup request (pending)
        MakeupLabRequest makeup2 = new MakeupLabRequest(
            "MR-002",
            "LAB002",
            "I-002",
            new Schedule(
                LocalDateTime.of(2024, 12, 3, 14, 0),
                LocalDateTime.of(2024, 12, 3, 16, 0)
            )
        );
        ds.getRequests().add(makeup2);
        
        return ds;
    }
}
