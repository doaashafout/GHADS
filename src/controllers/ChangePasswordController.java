package controllers;

import dao.UserDAO;
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
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import models.User;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ChangePasswordController implements Initializable {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changeButton;
    @FXML private Button backButton;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        Main.setupFontControls(tg, tg2, menubar);
    }

    @FXML
    private void changeHandle(ActionEvent event) {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError("Validation Error", "All fields are required.");
            return;
        }

        User user = SessionManager.getInstance().getCurrentUser();
        if (!user.getPassword().equals(current)) {
            showError("Error", "Current password is incorrect.");
            return;
        }

        if (!newPass.equals(confirm)) {
            showError("Error", "New passwords do not match.");
            return;
        }

        if (newPass.length() < 8) {
            showError("Validation Error", "Password must be at least 8 characters.");
            return;
        }

        if (userDAO.changePassword(user.getUserId(), newPass)) {
            user.setPassword(newPass);
            showInfo("Success", "Password changed successfully.");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showError("Error", "Could not change password.");
        }
    }

    @FXML
    private void backHandle(ActionEvent event) {
        User user = SessionManager.getInstance().getCurrentUser();
        if ("ADMIN".equals(user.getRole())) {
            navigateTo("../views/AdminDashboard.fxml");
        } else {
            navigateTo("../views/CoordinatorDashboard.fxml");
        }
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void exitHandle(ActionEvent event) { ((Stage) menubar.getScene().getWindow()).close(); }

    @FXML
    private void aboutHandle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About GHADS");
        alert.setHeaderText("Gaza Humanitarian Aid Distribution System");
        alert.setContentText("Version: 1.0\nDeveloper: Doaa Shafout\nIslamic University of Gaza");
        alert.showAndWait();
    }

    private void showInfo(String t, String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private void showError(String t, String m) { new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
}
