package controllers;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class CoordinatorDashboardController implements Initializable {

    @FXML private Label totalFamiliesLabel;
    @FXML private Label servedLabel;
    @FXML private Label notServedLabel;
    @FXML private Label welcomeLabel;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;

    private FamilyDAO familyDAO;
    private AidDistributionDAO distDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        familyDAO = new FamilyDAO();
        distDAO = new AidDistributionDAO();

        int orgId = SessionManager.getInstance().getCurrentUser().getOrgId();
        welcomeLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser().getFullName() + "!");

        int total = familyDAO.findAll().size();
        int served = distDAO.countDistinctFamiliesByOrg(orgId);
        int notServed = total - served;

        totalFamiliesLabel.setText(String.valueOf(total));
        servedLabel.setText(String.valueOf(served));
        notServedLabel.setText(String.valueOf(notServed));
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(CoordinatorDashboardController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert("Navigation Error", "Could not load screen: " + ex.getMessage());
        }
    }

    @FXML
    private void profileHandle(ActionEvent event) {
        navigateTo("../views/Profile.fxml");
    }

    @FXML
    private void familyRegistrationHandle(ActionEvent event) {
        navigateTo("../views/FamilyManagement.fxml");
    }

    @FXML
    private void aidDistributionHandle(ActionEvent event) {
        navigateTo("../views/AidDistributionForm.fxml");
    }

    @FXML
    private void changePasswordHandle(ActionEvent event) {
        navigateTo("../views/ChangePassword.fxml");
    }

    @FXML
    private void logoutHandle(ActionEvent event) {
        SessionManager.getInstance().logout();
        navigateTo("../views/Login.fxml");
    }

    @FXML
    private void exitHandle(ActionEvent event) {
        ((Stage) menubar.getScene().getWindow()).close();
    }

    @FXML
    private void aboutHandle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About GHADS");
        alert.setHeaderText("Gaza Humanitarian Aid Distribution System");
        alert.setContentText("Version: 1.0\n"
                           + "Developer: Doaa Shafout\n"
                           + "Programming III Lab - CSCI 2108\n"
                           + "Islamic University of Gaza");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
