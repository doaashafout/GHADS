package controllers;

import dao.FamilyDAO;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Family;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class FamilyController implements Initializable {

    @FXML private TableView<Family> table;
    @FXML private TableColumn<Family, Integer> familyIdTC;
    @FXML private TableColumn<Family, String> nameTC;
    @FXML private TableColumn<Family, String> nationalIdTC;
    @FXML private TableColumn<Family, String> phoneTC;
    @FXML private TableColumn<Family, String> locationTC;
    @FXML private TableColumn<Family, Integer> sizeTC;
    @FXML private TableColumn<Family, String> vulnTC;
    @FXML private TableColumn<Family, String> dateTC;

    @FXML private TextField householdNameField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField familySizeField;
    @FXML private TextField nationalIdField;
    @FXML private ComboBox<String> vulnCombo;
    @FXML private DatePicker regDatePicker;

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

    private FamilyDAO familyDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        familyDAO = new FamilyDAO();

        familyIdTC.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        nameTC.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        nationalIdTC.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        phoneTC.setCellValueFactory(new PropertyValueFactory<>("phone"));
        locationTC.setCellValueFactory(new PropertyValueFactory<>("location"));
        sizeTC.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        vulnTC.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        dateTC.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        vulnCombo.getItems().addAll("HIGH", "MEDIUM", "LOW");
        regDatePicker.setValue(LocalDate.now());

        loadTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                householdNameField.setText(sel.getHouseholdName());
                phoneField.setText(sel.getPhone());
                locationField.setText(sel.getLocation());
                familySizeField.setText(String.valueOf(sel.getFamilySize()));
                nationalIdField.setText(sel.getNationalId());
                vulnCombo.setValue(sel.getVulnerabilityLevel());
                if (sel.getRegistrationDate() != null)
                    regDatePicker.setValue(LocalDate.parse(sel.getRegistrationDate()));
            }
        });
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadTable() {
        table.setItems(FXCollections.observableArrayList(familyDAO.findAll()));
    }

    @FXML
    private void addHandle(ActionEvent event) {
        if (!validateForm()) return;
        if (familyDAO.nationalIdExists(nationalIdField.getText())) {
            showError("Validation Error", "National ID already exists.");
            return;
        }
        Family f = new Family(householdNameField.getText(), phoneField.getText(), locationField.getText(),
                Integer.parseInt(familySizeField.getText()), nationalIdField.getText(),
                vulnCombo.getValue(), regDatePicker.getValue().toString());
        if (familyDAO.insertOne(f)) {
            showInfo("Success", "Family registered successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not register family.");
        }
    }

    @FXML
    private void updateHandle(ActionEvent event) {
        Family sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select a family first."); return; }
        if (!validateForm()) return;
        sel.setHouseholdName(householdNameField.getText());
        sel.setPhone(phoneField.getText());
        sel.setLocation(locationField.getText());
        sel.setFamilySize(Integer.parseInt(familySizeField.getText()));
        sel.setNationalId(nationalIdField.getText());
        sel.setVulnerabilityLevel(vulnCombo.getValue());
        sel.setRegistrationDate(regDatePicker.getValue().toString());
        if (familyDAO.updateOne(sel)) {
            showInfo("Success", "Family updated successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not update family.");
        }
    }

    @FXML
    private void deleteHandle(ActionEvent event) {
        Family sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select a family first."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + sel.getHouseholdName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (familyDAO.deleteOne(sel)) {
                    showInfo("Success", "Family deleted.");
                    loadTable();
                    resetHandle(event);
                } else {
                    showError("Error", "Could not delete family.");
                }
            }
        });
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        householdNameField.clear(); phoneField.clear(); locationField.clear();
        familySizeField.clear(); nationalIdField.clear(); vulnCombo.setValue(null);
        regDatePicker.setValue(LocalDate.now());
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void refreshHandle(ActionEvent event) { loadTable(); }

    @FXML
    private void backHandle(ActionEvent event) {
        if (SessionManager.getInstance().isAdmin()) {
            navigateTo("../views/AdminDashboard.fxml");
        } else {
            navigateTo("../views/CoordinatorDashboard.fxml");
        }
    }

    private boolean validateForm() {
        if (householdNameField.getText().trim().isEmpty() || nationalIdField.getText().trim().isEmpty() ||
            vulnCombo.getValue() == null || regDatePicker.getValue() == null) {
            showError("Validation Error", "Household Name, National ID, Vulnerability Level, and Date are required.");
            return false;
        }
        if (familySizeField.getText().trim().isEmpty()) {
            showError("Validation Error", "Family size is required.");
            return false;
        }
        try { Integer.parseInt(familySizeField.getText()); } catch (NumberFormatException e) {
            showError("Validation Error", "Family size must be a number.");
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
            Logger.getLogger(FamilyController.class.getName()).log(Level.SEVERE, null, ex);
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
