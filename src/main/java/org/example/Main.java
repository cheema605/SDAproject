package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import java.nio.file.Path;

/**
 * Main - Application Entry Point
 * 
 * Displays login screen where users authenticate with their credentials.
 * Routes to role-specific dashboard after login.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        UserRepository userRepository = new UserRepository(Path.of("data", "users.dat"));
        LoginUI loginUI = new LoginUI(userRepository);
        loginUI.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
