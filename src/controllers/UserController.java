package controllers;

import dao.OrganizationDAO;
import dao.UserDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Organization;
import models.User;


import java.util.logging.Logger;
import app.Main;
import java.util.logging.Level;

public class UserController implements Initializable {

    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Integer> userIdTC;
    @FXML private TableColumn<User, String> usernameTC;
    @FXML private TableColumn<User, String> fullNameTC;
    @FXML private TableColumn<User, String> emailTC;
    @FXML private TableColumn<User, String> roleTC;

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<Organization> orgCombo;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button resetButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;

    private UserDAO userDAO;
    private OrganizationDAO orgDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        orgDAO = new OrganizationDAO();

        userIdTC.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameTC.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameTC.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailTC.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleTC.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleCombo.getItems().addAll("COORDINATOR");
        orgCombo.setItems(FXCollections.observableArrayList(orgDAO.findAll()));

        loadTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fullNameField.setText(sel.getFullName());
                usernameField.setText(sel.getUsername());
                emailField.setText(sel.getEmail());
                roleCombo.setValue(sel.getRole());
                if (sel.getOrgId() != null) {
                    orgCombo.getItems().stream().filter(o -> o.getOrgId() == sel.getOrgId()).findFirst().ifPresent(o -> orgCombo.setValue(o));
                }
                passwordField.clear();
            }
        });
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadTable() {
        table.setItems(FXCollections.observableArrayList(userDAO.findAll()));
    }

    @FXML
    private void addHandle(ActionEvent event) {
        if (passwordField.getText().trim().isEmpty()) {
            showError("Validation Error", "Password is required for new users.");
            return;
        }
        if (!validateForm()) return;
        if (userDAO.usernameExists(usernameField.getText())) {
            showError("Validation Error", "Username already exists.");
            return;
        }
        if (userDAO.emailExists(emailField.getText())) {
            showError("Validation Error", "Email already exists.");
            return;
        }
        Organization org = orgCombo.getValue();
        User u = new User(usernameField.getText(), passwordField.getText(), fullNameField.getText(),
                          emailField.getText(), "COORDINATOR", org != null ? org.getOrgId() : null);
        if (userDAO.insertOne(u)) {
            showInfo("Success", "User added successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not add user.");
        }
    }

    @FXML
    private void updateHandle(ActionEvent event) {
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select a user first."); return; }
        if (!validateForm()) return;
        sel.setFullName(fullNameField.getText());
        sel.setUsername(usernameField.getText());
        sel.setEmail(emailField.getText());
        Organization org = orgCombo.getValue();
        sel.setOrgId(org != null ? org.getOrgId() : null);
        if (userDAO.updateOne(sel)) {
            showInfo("Success", "User updated successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not update user.");
        }
    }

    @FXML
    private void deleteHandle(ActionEvent event) {
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select a user first."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + sel.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (userDAO.deleteOne(sel)) {
                    showInfo("Success", "User deleted.");
                    loadTable();
                    resetHandle(event);
                } else {
                    showError("Error", "Could not delete user.");
                }
            }
        });
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        fullNameField.clear(); usernameField.clear(); emailField.clear();
        passwordField.clear(); roleCombo.setValue(null); orgCombo.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void refreshHandle(ActionEvent event) { loadTable(); }

    @FXML
    private void backHandle(ActionEvent event) { navigateTo("../views/AdminDashboard.fxml"); }

    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty() || usernameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() || roleCombo.getValue() == null) {
            showError("Validation Error", "Full Name, Username, Email, and Role are required.");
            return false;
        }
        if (passwordField.getText().length() > 0 && passwordField.getText().length() < 8) {
            showError("Validation Error", "Password must be at least 8 characters.");
            return false;
        }
        return true;
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
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
