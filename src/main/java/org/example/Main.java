package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import java.nio.file.Path;

// Main class to start the app
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // create user repo and login screen
        UserRepository userRepository = new UserRepository(Path.of("data", "users.dat"));
        LoginUI loginUI = new LoginUI(userRepository);
        loginUI.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
