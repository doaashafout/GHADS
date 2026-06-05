package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.AuthService;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authService = new AuthService();
    }

    @FXML
    private void loginHandle(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please enter username and password.");
            return;
        }

        User user = authService.login(username, password);
        if (user == null) {
            showAlert("Login Failed", "Invalid username or password.");
            return;
        }

        SessionManager.getInstance().setCurrentUser(user);

        try {
            Parent root;
            if ("ADMIN".equals(user.getRole())) {
                root = FXMLLoader.load(getClass().getResource("../views/AdminDashboard.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("../views/CoordinatorDashboard.fxml"));
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("GHADS - " + user.getFullName());
        } catch (Exception ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert("Error", "Could not load dashboard: " + ex.getMessage());
        }
    }

    @FXML
    private void exitHandle(ActionEvent event) {
        ((Stage) exitButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
