package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

/**
 * LoginUI provides authentication screen for users to log in with role-based access.
 * Single Responsibility: Handle user login and authentication.
 */
public class LoginUI {
    
    private final UserRepository userRepository;
    private final List<User> users;
    private Runnable onLoginSuccess;
    private User currentUser;
    
    public LoginUI(UserRepository userRepository) throws Exception {
        this.userRepository = userRepository;
        this.users = userRepository.loadUsers();
    }
    
    /**
     * Shows the login screen.
     */
    public void show(Stage stage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + ";");
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        
        // Title
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label subtitle = new Label("User Login");
        subtitle.setStyle(StyleManager.HEADER_SUBTITLE_STYLE);
        
        // Login form
        VBox loginForm = new VBox(15);
        loginForm.setStyle("-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                          "-fx-border-radius: 8; -fx-padding: 30;");
        loginForm.setPrefWidth(350);
        
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle(StyleManager.LABEL_STYLE);
        TextField usernameField = StyleManager.createStyledTextField("Enter username");
        
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(StyleManager.LABEL_STYLE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-border-color: #BCC1C6; " +
                               "-fx-border-radius: 4; -fx-padding: 8;");
        passwordField.setMinHeight(32);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 11;");
        
        Button loginBtn = StyleManager.createStyledButton("Login", StyleManager.PRIMARY_COLOR);
        loginBtn.setPrefWidth(350);
        
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            
            User user = authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                openRoleDashboard(stage, user);
            } else {
                errorLabel.setText("Invalid username or password");
                passwordField.clear();
            }
        });
        
        // Demo users info
        Label demoLabel = new Label("Demo Credentials:");
        demoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");
        
        TextArea demoInfo = new TextArea(
            "Academic Officer: officer / pass123\n" +
            "Attendant (CS): attendant_cs / pass123\n" +
            "HOD: hod / pass123\n" +
            "Instructor: instructor1 / pass123\n" +
            "TA: ta1 / pass123"
        );
        demoInfo.setEditable(false);
        demoInfo.setWrapText(true);
        demoInfo.setPrefHeight(100);
        demoInfo.setStyle("-fx-font-size: 10; -fx-control-inner-background: #F5F5F5;");
        
        loginForm.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            errorLabel,
            loginBtn,
            new Separator(),
            demoLabel,
            demoInfo
        );
        
        root.getChildren().addAll(title, subtitle, loginForm);
        
        Scene scene = new Scene(root, 500, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System - Login");
        stage.show();
    }
    
    /**
     * Authenticates user credentials against stored users.
     */
    private User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Opens the role-specific dashboard after successful login.
     */
    private void openRoleDashboard(Stage stage, User user) {
        switch (user.getRole()) {
            case ACADEMIC_OFFICER:
                new AcademicOfficerView(stage, user).show();
                break;
            case ATTENDANT:
                new AttendantView(stage, user).show();
                break;
            case HOD:
                new HODView(stage, user).show();
                break;
            case INSTRUCTOR:
                new InstructorView(stage, user).show();
                break;
            case TA:
                new TAView(stage, user).show();
                break;
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
