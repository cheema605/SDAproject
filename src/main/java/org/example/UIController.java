package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.io.IOException;
import java.time.LocalDateTime;

// UIController manages all UI control logic and event handling.
public class UIController {

    // list of labs
    private ObservableList<Lab> labs = FXCollections.observableArrayList();
    private LabRepository repository;
    private DialogFactory dialogFactory;
    private ReportGenerator reportGenerator;

    private DataStore dataStore;
    private AcademicOfficer academicOfficer;

    // constructor
    public UIController(LabRepository repository) {
        this.repository = repository;
        this.dialogFactory = new DialogFactory(labs);
        this.reportGenerator = new ReportGenerator(labs);
        try {
            this.dataStore = repository.load();
            if (this.dataStore.getLabs().isEmpty()) {
                this.dataStore = generateDefaultData();
                repository.save(this.dataStore);
            }
        } catch (Exception e) {
            this.dataStore = generateDefaultData();
            try {
                repository.save(this.dataStore);
            } catch (IOException ignored) {
            }
        }
        this.academicOfficer = new AcademicOfficer(dataStore);
        this.labs.setAll(dataStore.getLabs());
    }

    private DataStore generateDefaultData() {
        DataStore ds = new DataStore();

        // Create instructors with Pakistani names
        Instructor dr_ahmed = new Instructor("I-001", "Dr. Taimoor Pasha");
        Instructor dr_fatima = new Instructor("I-002", "Dr. Ayesha Siddiqui");
        Instructor dr_hassan = new Instructor("I-003", "Dr. Bilal Ahmed");

        ds.getInstructors().add(dr_ahmed);
        ds.getInstructors().add(dr_fatima);
        ds.getInstructors().add(dr_hassan);

        // Create TAs with Pakistani names
        TA ali_khan = new TA("TA-001", "Hamza Khan");
        TA sara_hussain = new TA("TA-002", "Fatima Batool");
        TA usman_baig = new TA("TA-003", "Usman Ghani");
        TA ayesha_malik = new TA("TA-004", "Zainab Bibi");

        ds.getTas().add(ali_khan);
        ds.getTas().add(sara_hussain);
        ds.getTas().add(usman_baig);
        ds.getTas().add(ayesha_malik);

        // Lab 1: Database Systems
        Lab lab1 = new Lab("LAB001", "Database Systems");
        lab1.setVenue(new Venue("CS Building", "Room 101"));
        lab1.setSchedule(new Schedule(
                LocalDateTime.of(2024, 11, 18, 10, 0),
                LocalDateTime.of(2024, 11, 18, 12, 0)));
        lab1.setInstructor(dr_ahmed);
        lab1.addTA(ali_khan);
        lab1.addTA(sara_hussain);

        ds.getLabs().add(lab1);

        // Lab 2: Web Development
        Lab lab2 = new Lab("LAB002", "Web Development");
        lab2.setVenue(new Venue("CS Building", "Room 102"));
        lab2.setSchedule(new Schedule(
                LocalDateTime.of(2024, 11, 18, 14, 0),
                LocalDateTime.of(2024, 11, 18, 16, 0)));
        lab2.setInstructor(dr_fatima);
        lab2.addTA(usman_baig);

        ds.getLabs().add(lab2);

        return ds;
    }

    public enum LabViewMode {
        ALL, ACTIVE_NOW, TODAY
    }

    // get labs for a specific user based on mode
    public ObservableList<Lab> getLabsForUser(User user, LabViewMode mode) {
        ObservableList<Lab> result = FXCollections.observableArrayList();
        LocalDateTime now = LocalDateTime.now();

        // loop through all labs
        for (Lab lab : dataStore.getLabs()) {
            boolean includeByActive = true;
            if (mode == LabViewMode.ACTIVE_NOW) {
                includeByActive = false;
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null && s.getExpectedEnd() != null) {
                    if (!now.isBefore(s.getExpectedStart()) && !now.isAfter(s.getExpectedEnd()))
                        includeByActive = true;
                }
                if (!includeByActive) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null && ts.getActualEnd() != null) {
                            if (!now.isBefore(ts.getActualStart()) && !now.isAfter(ts.getActualEnd())) {
                                includeByActive = true;
                                break;
                            }
                        }
                    }
                }
            } else if (mode == LabViewMode.TODAY) {
                includeByActive = false;
                java.time.LocalDate today = now.toLocalDate();
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null) {
                    if (s.getExpectedStart().toLocalDate().equals(today))
                        includeByActive = true;
                }
                if (!includeByActive) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null) {
                            if (ts.getActualStart().toLocalDate().equals(today)) {
                                includeByActive = true;
                                break;
                            }
                        }
                    }
                }
            } // else ALL -> includeByActive stays true

            if (!includeByActive)
                continue;

            // check user role
            switch (user.getRole()) {
                case ACADEMIC_OFFICER:
                    result.add(lab);
                    break;
                case ATTENDANT:
                    if (lab.getVenue() != null && lab.getVenue().getBuilding() != null
                            && user.getBuilding() != null
                            && lab.getVenue().getBuilding().equalsIgnoreCase(user.getBuilding())) {
                        result.add(lab);
                    }
                    break;
                case HOD:
                    result.add(lab);
                    break;
                case INSTRUCTOR:
                    if (lab.getInstructor() != null && lab.getInstructor().getName() != null
                            && lab.getInstructor().getName().equalsIgnoreCase(user.getName())) {
                        result.add(lab);
                    }
                    break;
                case TA:
                    for (TA ta : lab.getTas()) {
                        if (ta.getName() != null && ta.getName().equalsIgnoreCase(user.getName())) {
                            result.add(lab);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    public TableView<Lab> createLabsTable() {
        return createLabsTable(null);
    }

    // create the table view
    public TableView<Lab> createLabsTable(User user) {
        TableView<Lab> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setStyle(StyleManager.TABLE_STYLE);

        // Lab ID column
        TableColumn<Lab, String> idCol = new TableColumn<>("Lab ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue() == null || cell.getValue().getId() == null ? "" : cell.getValue().getId()));
        idCol.setStyle("-fx-alignment: CENTER;");

        // Lab name column
        TableColumn<Lab, String> nameCol = new TableColumn<>("Lab Name");
        nameCol.setPrefWidth(140);
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue() == null || cell.getValue().getName() == null ? "" : cell.getValue().getName()));

        // Venue column
        TableColumn<Lab, String> venueCol = new TableColumn<>("Location");
        venueCol.setPrefWidth(160);
        venueCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue() == null || cell.getValue().getVenue() == null ? "N/A"
                        : cell.getValue().getVenue().toString()));

        table.getColumns().addAll(idCol, nameCol, venueCol);

        // add extra columns based on role
        if (user == null || user.getRole() == User.Role.ACADEMIC_OFFICER || user.getRole() == User.Role.HOD) {
            TableColumn<Lab, String> instrCol = new TableColumn<>("Instructor");
            instrCol.setPrefWidth(140);
            instrCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                    cell.getValue() == null || cell.getValue().getInstructor() == null ? "Unassigned"
                            : cell.getValue().getInstructor().getName()));

            TableColumn<Lab, String> tasCol = new TableColumn<>("Teaching Assistants");
            tasCol.setPrefWidth(180);
            tasCol.setCellValueFactory(cell -> {
                if (cell.getValue() == null || cell.getValue().getTas().isEmpty())
                    return new javafx.beans.property.SimpleStringProperty("None");
                StringBuilder sb = new StringBuilder();
                for (TA ta : cell.getValue().getTas()) {
                    if (sb.length() > 0)
                        sb.append(", ");
                    sb.append(ta.getName());
                }
                return new javafx.beans.property.SimpleStringProperty(sb.toString());
            });

            table.getColumns().addAll(instrCol, tasCol);
        } else if (user.getRole() == User.Role.INSTRUCTOR) {
            TableColumn<Lab, String> tasCol = new TableColumn<>("Teaching Assistants");
            tasCol.setPrefWidth(160);
            tasCol.setCellValueFactory(cell -> {
                if (cell.getValue() == null || cell.getValue().getTas().isEmpty())
                    return new javafx.beans.property.SimpleStringProperty("None");
                StringBuilder sb = new StringBuilder();
                for (TA ta : cell.getValue().getTas()) {
                    if (sb.length() > 0)
                        sb.append(", ");
                    sb.append(ta.getName());
                }
                return new javafx.beans.property.SimpleStringProperty(sb.toString());
            });
            table.getColumns().add(tasCol);
        }

        return table;
    }

    // create header
    public VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);

        Label subtitle = new Label("University Lab & Scheduling Administration Platform");
        subtitle.setStyle(StyleManager.HEADER_SUBTITLE_STYLE);

        VBox headerBox = new VBox(6, title, subtitle);
        headerBox.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                "-fx-border-width: 0 0 2 0;");
        headerBox.setPadding(new Insets(20));

        return headerBox;
    }

    // create table container
    public VBox createTableContainer(TableView<Lab> table) {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                "-fx-border-width: 1;");
        container.setPadding(new Insets(15));
        container.setSpacing(10);

        Label tableTitle = new Label("Active Labs");
        tableTitle.setStyle(
                "-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");

        VBox.setVgrow(table, Priority.ALWAYS);
        container.getChildren().addAll(tableTitle, table);

        return container;
    }

    // create control panel
    public VBox createControlPanel() {
        // buttons
        Button loadBtn = StyleManager.createStyledButton("📂 Load", "#4CAF50");
        Button saveBtn = StyleManager.createStyledButton("💾 Save", "#2196F3");
        Button addBtn = StyleManager.createStyledButton("➕ Add Lab", StyleManager.PRIMARY_COLOR);

        loadBtn.setOnAction(e -> handleLoad());
        saveBtn.setOnAction(e -> handleSave());
        addBtn.setOnAction(e -> handleAddLab());

        HBox primaryActions = new HBox(10, loadBtn, saveBtn, addBtn);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                "-fx-border-width: 1 0 0 0;");

        // more buttons
        Button assignBtn = StyleManager.createStyledButton("👥 Assign Staff", StyleManager.PRIMARY_COLOR);
        Button enterTSBtn = StyleManager.createStyledButton("⏱️ TimeSheet", "#FF9800");
        Button makeupBtn = StyleManager.createStyledButton("🔄 Makeup Lab", "#9C27B0");

        assignBtn.setOnAction(e -> handleAssignStaff());
        enterTSBtn.setOnAction(e -> handleEnterTimeSheet());
        makeupBtn.setOnAction(e -> handleRequestMakeup());

        HBox secondaryActions = new HBox(10, assignBtn, enterTSBtn, makeupBtn);
        secondaryActions.setPadding(new Insets(10));
        secondaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + ";");

        // report buttons
        Button scheduleReportBtn = StyleManager.createStyledButton("📋 Schedule Report", "#607D8B");
        Button timesheetReportBtn = StyleManager.createStyledButton("📊 Timesheet Report", "#607D8B");
        Button labReportBtn = StyleManager.createStyledButton("📈 Lab Report", "#607D8B");

        scheduleReportBtn.setOnAction(e -> reportGenerator.generateWeeklyScheduleReport());
        timesheetReportBtn.setOnAction(e -> reportGenerator.generateWeeklyTimeSheetReport());
        labReportBtn.setOnAction(e -> handleLabReport());

        HBox reportActions = new HBox(10, scheduleReportBtn, timesheetReportBtn, labReportBtn);
        reportActions.setPadding(new Insets(10));
        reportActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                "-fx-border-width: 1 0 0 0;");

        VBox controlBox = new VBox(primaryActions, secondaryActions, reportActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");

        return controlBox;
    }

    // handle load button
    private void handleLoad() {
        try {
            DataStore ds = repository.load();
            labs.setAll(ds.getLabs());
            dataStore = ds;
            AlertHelper.showSuccess("Load Successful", "Loaded " + ds.getLabs().size() + " labs from disk");
        } catch (Exception ex) {
            AlertHelper.showError("Load Failed", "Could not load data: " + ex.getMessage());
        }
    }

    // handle save button
    private void handleSave() {
        try {
            dataStore.getLabs().clear();
            dataStore.getLabs().addAll(labs);
            repository.save(dataStore);
            AlertHelper.showSuccess("Save Successful", "Saved " + labs.size() + " labs to disk");
        } catch (IOException ex) {
            AlertHelper.showError("Save Failed", "Could not save data: " + ex.getMessage());
        }
    }

    public void handleAddLab() {
        dialogFactory.createAddLabDialog().showAndWait().ifPresent(lab -> {
            labs.add(lab);
            handleSave(); // Auto-save
            AlertHelper.showSuccess("Lab Created", "Lab '" + lab.getName() + "' has been created successfully");
        });
    }

    public void handleAssignStaff() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null)
            return;

        dialogFactory.createAssignStaffDialog(sel).showAndWait().ifPresent(result -> {
            if (result.isInstructor) {
                academicOfficer.assignInstructor(sel.getId(), result.name);
                AlertHelper.showSuccess("Assigned", "Instructor assigned to " + sel.getId());
            } else {
                academicOfficer.assignTA(sel.getId(), result.name);
                AlertHelper.showSuccess("Assigned", "TA assigned to " + sel.getId());
            }
            handleSave(); // Auto-save
            refreshTable();
        });
    }

    public void handleEnterTimeSheet() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null)
            return;

        dialogFactory.createEnterTimeSheetDialog(sel).showAndWait().ifPresent(ts -> {
            sel.addSession(ts);
            handleSave(); // Auto-save
            AlertHelper.showSuccess("TimeSheet Added", "Entry recorded for " + sel.getId());
        });
    }

    public void handleRequestMakeup() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null)
            return;

        dialogFactory.createRequestMakeupDialog(sel).showAndWait().ifPresent(req -> {
            try {
                dataStore.getRequests().add(req);
                handleSave(); // Auto-save
                AlertHelper.showSuccess("Makeup Requested", "Request submitted for approval");
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Could not save request: " + ex.getMessage());
            }
        });
    }

    public void handleLabReport() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel != null) {
            reportGenerator.generateLabTimeSheetReport(sel);
        }
    }

    // get labs for user with active only flag
    public ObservableList<Lab> getLabsForUser(User user, boolean activeOnly) {
        ObservableList<Lab> result = FXCollections.observableArrayList();
        LocalDateTime now = LocalDateTime.now();

        for (Lab lab : dataStore.getLabs()) {
            // check active state
            boolean active = true;
            if (activeOnly) {
                active = false;
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null && s.getExpectedEnd() != null) {
                    if (!now.isBefore(s.getExpectedStart()) && !now.isAfter(s.getExpectedEnd()))
                        active = true;
                }
                if (!active) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null && ts.getActualEnd() != null) {
                            if (!now.isBefore(ts.getActualStart()) && !now.isAfter(ts.getActualEnd())) {
                                active = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!active)
                continue;

            // role-based scoping
            switch (user.getRole()) {
                case ACADEMIC_OFFICER:
                    result.add(lab);
                    break;
                case ATTENDANT:
                    if (lab.getVenue() != null && lab.getVenue().getBuilding() != null
                            && user.getBuilding() != null
                            && lab.getVenue().getBuilding().equalsIgnoreCase(user.getBuilding())) {
                        result.add(lab);
                    }
                    break;
                case HOD:
                    // HOD sees all labs for now
                    result.add(lab);
                    break;
                case INSTRUCTOR:
                    if (lab.getInstructor() != null && lab.getInstructor().getName() != null
                            && lab.getInstructor().getName().equalsIgnoreCase(user.getName())) {
                        result.add(lab);
                    }
                    break;
                case TA:
                    for (TA ta : lab.getTas()) {
                        if (ta.getName() != null && ta.getName().equalsIgnoreCase(user.getName())) {
                            result.add(lab);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    private void refreshTable() {
        labs.clear();
        labs.addAll(dataStore.getLabs());
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    public ObservableList<Lab> getLabs() {
        return labs;
    }
}
