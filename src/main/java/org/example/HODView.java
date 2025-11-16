package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Path;

/**
 * HODView - Dashboard for Head of Department role.
 * Allows: Generating reports (weekly schedule, weekly timesheet, semester lab report).
 */
public class HODView {
    
    private final Stage stage;
    private final User user;
    private final LabRepository labRepository;
    private final UIController uiController;
    
    public HODView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.labRepository = new LabRepository(Path.of("data", "datastore.dat"));
        this.uiController = new UIController(labRepository);
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        
        // Header with user info
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content - labs table
        TableView<Lab> table = uiController.createLabsTable(user);
        table.setItems(uiController.getLabsForUser(user, UIController.LabViewMode.ACTIVE_NOW));
        root.setCenter(uiController.createTableContainer(table));
        
        // Controls - only HOD features (reports)
        VBox controls = createControlPanel(table);
        root.setBottom(controls);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System - Head of Department");
        stage.show();
    }
    
    private VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label userInfo = new Label("Logged in as: " + user.getName() + " (Head of Department)");
        userInfo.setStyle("-fx-font-size: 12; -fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");
        
        Button logoutBtn = StyleManager.createStyledButton("Logout", "#F44336");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setOnAction(e -> logout());
        
        HBox topRight = new HBox(10);
        topRight.setPadding(new Insets(10, 20, 10, 0));
        topRight.getChildren().add(logoutBtn);
        
        VBox headerContent = new VBox(6, title, userInfo);
        headerContent.setPadding(new Insets(20));
        headerContent.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                              "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                              "-fx-border-width: 0 0 2 0;");
        
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(headerContent);
        headerPane.setRight(topRight);
        
        return new VBox(headerPane);
    }
    
    private VBox createControlPanel(TableView<Lab> table) {
        Button weeklyScheduleBtn = StyleManager.createStyledButton("📋 Weekly Schedule Report", "#607D8B");
        Button weeklyTimesheetBtn = StyleManager.createStyledButton("📊 Weekly Timesheet Report", "#607D8B");
        Button labReportBtn = StyleManager.createStyledButton("📈 Lab Semester Report", "#607D8B");
        Button exportBtn = StyleManager.createStyledButton("💾 Export Reports", "#2196F3");

        weeklyScheduleBtn.setOnAction(e -> uiController.getReportGenerator().generateWeeklyScheduleReport());
        weeklyTimesheetBtn.setOnAction(e -> uiController.getReportGenerator().generateWeeklyTimeSheetReport());
        labReportBtn.setOnAction(e -> uiController.handleLabReport());
        exportBtn.setOnAction(e -> openExportDialog());

        ChoiceBox<UIController.LabViewMode> viewChoice = new ChoiceBox<>();
        viewChoice.getItems().addAll(UIController.LabViewMode.ACTIVE_NOW, UIController.LabViewMode.ALL, UIController.LabViewMode.TODAY);
        viewChoice.setValue(UIController.LabViewMode.ACTIVE_NOW);
        viewChoice.setOnAction(e -> table.setItems(uiController.getLabsForUser(user, viewChoice.getValue())));

        HBox reportActions = new HBox(10, weeklyScheduleBtn, weeklyTimesheetBtn, labReportBtn, exportBtn, new Label(" "), viewChoice);
        reportActions.setPadding(new Insets(10));
        reportActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                      "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                      "-fx-border-width: 1 0 0 0;");

        VBox controlBox = new VBox(reportActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");

        return controlBox;
    }
    
    private void openExportDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Export Reports");
        dialog.setHeaderText("Choose export format and reports");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        CheckBox weeklyScheduleCheck = new CheckBox("Weekly Schedule Report");
        CheckBox weeklyTimesheetCheck = new CheckBox("Weekly Timesheet Report");
        CheckBox semesterLabCheck = new CheckBox("Lab Semester Report");
        weeklyScheduleCheck.setSelected(true);
        weeklyTimesheetCheck.setSelected(true);
        semesterLabCheck.setSelected(true);
        
        ComboBox<String> formatCombo = new ComboBox<>();
        formatCombo.getItems().addAll("PDF", "CSV", "Excel");
        formatCombo.setValue("PDF");
        formatCombo.setStyle("-fx-font-size: 12;");
        
        VBox reportBox = new VBox(10, weeklyScheduleCheck, weeklyTimesheetCheck, semesterLabCheck);
        reportBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        
        content.getChildren().addAll(
            new Label("Select reports to export:"),
            reportBox,
            new Label("Export format:"),
            formatCombo
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(result -> {
            String format = formatCombo.getValue() != null ? formatCombo.getValue() : "PDF";
            AlertHelper.showSuccess("Export", "Reports exported as " + format);
        });
    }
    
    private void logout() {
        try {
            LoginUI loginUI = new LoginUI(new UserRepository(Path.of("data", "users.dat")));
            loginUI.show(stage);
        } catch (Exception ex) {
            AlertHelper.showError("Error", "Failed to load login screen");
        }
    }
}
