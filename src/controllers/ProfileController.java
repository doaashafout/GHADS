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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import models.User;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ProfileController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField roleField;
    @FXML private TextField orgField;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button backButton;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;

    private UserDAO userDAO;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();

        fullNameField.setText(currentUser.getFullName());
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        roleField.setText(currentUser.getRole());
        if (currentUser.getOrgId() != null) {
            orgField.setText(String.valueOf(currentUser.getOrgId()));
        }

        setEditable(false);
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void setEditable(boolean b) {
        fullNameField.setEditable(b);
        usernameField.setEditable(b);
        emailField.setEditable(b);
    }

    @FXML
    private void editHandle(ActionEvent event) {
        setEditable(true);
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }

    @FXML
    private void saveHandle(ActionEvent event) {
        if (fullNameField.getText().trim().isEmpty() || usernameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty()) {
            showError("Validation Error", "All fields are required.");
            return;
        }

        currentUser.setFullName(fullNameField.getText());
        currentUser.setUsername(usernameField.getText());
        currentUser.setEmail(emailField.getText());

        if (userDAO.updateOne(currentUser)) {
            showInfo("Success", "Profile updated successfully.");
            setEditable(false);
            saveButton.setDisable(true);
            editButton.setDisable(false);
        } else {
            showError("Error", "Could not update profile.");
        }
    }

    @FXML
    private void backHandle(ActionEvent event) {
        navigateTo("../views/CoordinatorDashboard.fxml");
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
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
