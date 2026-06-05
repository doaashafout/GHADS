package controllers;

import dao.OrganizationDAO;
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
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Organization;


import java.util.logging.Logger;
import app.Main;
import java.util.logging.Level;

public class OrganizationController implements Initializable {

    @FXML private TableView<Organization> table;
    @FXML private TableColumn<Organization, Integer> orgIdTC;
    @FXML private TableColumn<Organization, String> nameTC;
    @FXML private TableColumn<Organization, String> typeTC;
    @FXML private TableColumn<Organization, String> contactTC;

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField contactField;

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

    private OrganizationDAO orgDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orgDAO = new OrganizationDAO();

        orgIdTC.setCellValueFactory(new PropertyValueFactory<>("orgId"));
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeTC.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactTC.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        loadTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                nameField.setText(sel.getName());
                typeField.setText(sel.getType());
                contactField.setText(sel.getContactInfo());
            }
        });
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadTable() {
        table.setItems(FXCollections.observableArrayList(orgDAO.findAll()));
    }

    @FXML
    private void addHandle(ActionEvent event) {
        if (!validateForm()) return;
        Organization o = new Organization(nameField.getText(), typeField.getText(), contactField.getText());
        if (orgDAO.insertOne(o)) {
            showInfo("Success", "Organization added successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not add organization.");
        }
    }

    @FXML
    private void updateHandle(ActionEvent event) {
        Organization sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select an organization first."); return; }
        if (!validateForm()) return;
        sel.setName(nameField.getText());
        sel.setType(typeField.getText());
        sel.setContactInfo(contactField.getText());
        if (orgDAO.updateOne(sel)) {
            showInfo("Success", "Organization updated successfully.");
            loadTable();
            resetHandle(event);
        } else {
            showError("Error", "Could not update organization.");
        }
    }

    @FXML
    private void deleteHandle(ActionEvent event) {
        Organization sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Error", "Please select an organization first."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (orgDAO.deleteOne(sel)) {
                    showInfo("Success", "Organization deleted.");
                    loadTable();
                    resetHandle(event);
                } else {
                    showError("Error", "Could not delete organization.");
                }
            }
        });
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        nameField.clear();
        typeField.clear();
        contactField.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void refreshHandle(ActionEvent event) {
        loadTable();
    }

    @FXML
    private void backHandle(ActionEvent event) {
        navigateTo("../views/AdminDashboard.fxml");
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty()) {
            showError("Validation Error", "Name and Type are required.");
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
            Logger.getLogger(OrganizationController.class.getName()).log(Level.SEVERE, null, ex);
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
