package org.example;

import java.time.LocalDateTime;

public class SampleDataInitializer {
    
    public static DataStore generateSampleData() {
        DataStore ds = new DataStore();
        
        // Create instructors with Pakistani names
        Instructor dr_ahmed = new Instructor("I-001", "Dr. Ahmed Khan");
        Instructor dr_fatima = new Instructor("I-002", "Dr. Fatima Ali");
        Instructor dr_hassan = new Instructor("I-003", "Dr. Hassan Malik");
        
        ds.getInstructors().add(dr_ahmed);
        ds.getInstructors().add(dr_fatima);
        ds.getInstructors().add(dr_hassan);
        
        // Create TAs with Pakistani names
        TA ali_khan = new TA("TA-001", "Ali Khan");
        TA sara_hussain = new TA("TA-002", "Sara Hussain");
        TA usman_baig = new TA("TA-003", "Usman Baig");
        TA ayesha_malik = new TA("TA-004", "Ayesha Malik");
        
        ds.getTas().add(ali_khan);
        ds.getTas().add(sara_hussain);
        ds.getTas().add(usman_baig);
        ds.getTas().add(ayesha_malik);
        
        // Lab 1: Database Systems
        Lab lab1 = new Lab("LAB001", "Database Systems");
        lab1.setVenue(new Venue("CS Building", "Room 101"));
        lab1.setSchedule(new Schedule(
            LocalDateTime.of(2024, 11, 18, 10, 0),
            LocalDateTime.of(2024, 11, 18, 12, 0)
        ));
        lab1.setInstructor(dr_ahmed);
        lab1.addTA(ali_khan);
        lab1.addTA(sara_hussain);
        
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
        lab2.setInstructor(dr_fatima);
        lab2.addTA(usman_baig);
        
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
        lab3.setInstructor(dr_hassan);
        lab3.addTA(ayesha_malik);
        lab3.addTA(ali_khan);
        
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
