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
 * AcademicOfficerView - Dashboard for Academic Officer role.
 * Allows: Creating labs, assigning instructors/TAs, managing basic data.
 */
public class AcademicOfficerView {
    
    private final Stage stage;
    private final User user;
    private final LabRepository labRepository;
    private final UIController uiController;
    
    public AcademicOfficerView(Stage stage, User user) {
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
        root.setCenter(uiController.createTableContainer(uiController.createLabsTable()));
        
        // Controls - only Academic Officer features
        VBox controls = createControlPanel();
        root.setBottom(controls);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System - Academic Officer");
        stage.show();
    }
    
    private VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label userInfo = new Label("Logged in as: " + user.getName() + " (Academic Officer)");
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
    
    private VBox createControlPanel() {
        Button addLabBtn = StyleManager.createStyledButton("➕ Add Lab", StyleManager.PRIMARY_COLOR);
        Button assignStaffBtn = StyleManager.createStyledButton("👥 Assign Staff", StyleManager.PRIMARY_COLOR);
        Button scheduleBtn = StyleManager.createStyledButton("📅 Set Schedule", "#2196F3");
        
        addLabBtn.setOnAction(e -> uiController.handleAddLab());
        assignStaffBtn.setOnAction(e -> uiController.handleAssignStaff());
        scheduleBtn.setOnAction(e -> openScheduleDialog());
        
        HBox primaryActions = new HBox(10, addLabBtn, assignStaffBtn, scheduleBtn);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                               "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                               "-fx-border-width: 1 0 0 0;");
        
        VBox controlBox = new VBox(primaryActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        
        return controlBox;
    }
    
    private void openScheduleDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Lab Schedules");
        dialog.setHeaderText("Set schedule for selected lab");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        ComboBox<String> labCombo = new ComboBox<>();
        labCombo.setStyle("-fx-font-size: 12;");
        labCombo.setPromptText("Select a lab");
        labCombo.setPrefWidth(300);
        
        DatePicker startDate = new DatePicker();
        startDate.setStyle("-fx-font-size: 12;");
        
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        dayCombo.setStyle("-fx-font-size: 12;");
        dayCombo.setPromptText("Select day");
        
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 10);
        hourSpinner.setStyle("-fx-font-size: 12;");
        
        content.getChildren().addAll(
            new Label("Lab:"), labCombo,
            new Label("Start Date:"), startDate,
            new Label("Day:"), dayCombo,
            new Label("Start Hour:"), hourSpinner
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(result -> {
            if (dayCombo.getValue() != null && startDate.getValue() != null) {
                AlertHelper.showSuccess("Schedule", "Schedule saved for " + dayCombo.getValue());
            }
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
