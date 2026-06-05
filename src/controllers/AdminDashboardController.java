package controllers;

import dao.AidDistributionDAO;
import dao.FamilyDAO;
import dao.OrganizationDAO;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class AdminDashboardController implements Initializable {

    @FXML private Label totalOrgsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFamiliesLabel;
    @FXML private Label servedFamiliesLabel;
    @FXML private Label notServedLabel;
    @FXML private Label welcomeLabel;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;

    private OrganizationDAO orgDAO;
    private UserDAO userDAO;
    private FamilyDAO familyDAO;
    private AidDistributionDAO distDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orgDAO = new OrganizationDAO();
        userDAO = new UserDAO();
        familyDAO = new FamilyDAO();
        distDAO = new AidDistributionDAO();

        welcomeLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser().getFullName() + "!");
        loadStats();
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadStats() {
        int totalOrgs = orgDAO.findAll().size();
        int totalUsers = userDAO.findAll().size();
        int totalFamilies = familyDAO.findAll().size();
        int served = distDAO.findAll().size();
        int notServed = familyDAO.findNotServed().size();

        totalOrgsLabel.setText(String.valueOf(totalOrgs));
        totalUsersLabel.setText(String.valueOf(totalUsers));
        totalFamiliesLabel.setText(String.valueOf(totalFamilies));
        servedFamiliesLabel.setText(String.valueOf(served));
        notServedLabel.setText(String.valueOf(notServed));
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(AdminDashboardController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert("Navigation Error", "Could not load screen: " + ex.getMessage());
        }
    }

    @FXML
    private void orgManagementHandle(ActionEvent event) {
        navigateTo("../views/OrganizationManagement.fxml");
    }

    @FXML
    private void userManagementHandle(ActionEvent event) {
        navigateTo("../views/UserManagement.fxml");
    }

    @FXML
    private void familyManagementHandle(ActionEvent event) {
        navigateTo("../views/FamilyManagement.fxml");
    }

    @FXML
    private void aidDistributionHandle(ActionEvent event) {
        navigateTo("../views/AidDistributionManagement.fxml");
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
