package controllers;

import dao.AidDistributionDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.AidDistribution;
import models.Organization;


import java.util.logging.Logger;
import app.Main;
import java.util.logging.Level;

public class AidDistributionController implements Initializable {

    @FXML private TableView<AidDistribution> table;
    @FXML private TableColumn<AidDistribution, Integer> distIdTC;
    @FXML private TableColumn<AidDistribution, String> familyNameTC;
    @FXML private TableColumn<AidDistribution, String> orgNameTC;
    @FXML private TableColumn<AidDistribution, String> dateTC;
    @FXML private TableColumn<AidDistribution, String> aidTypeTC;

    @FXML private ComboBox<Organization> filterOrgCombo;
    @FXML private Button filterButton;
    @FXML private Button showAllButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private javafx.scene.control.ToggleGroup tg;
    @FXML private javafx.scene.control.ToggleGroup tg2;

    private AidDistributionDAO distDAO;
    private OrganizationDAO orgDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        distDAO = new AidDistributionDAO();
        orgDAO = new OrganizationDAO();

        distIdTC.setCellValueFactory(new PropertyValueFactory<>("distributionId"));
        familyNameTC.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        orgNameTC.setCellValueFactory(new PropertyValueFactory<>("orgName"));
        dateTC.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));
        aidTypeTC.setCellValueFactory(new PropertyValueFactory<>("aidType"));

        filterOrgCombo.setItems(FXCollections.observableArrayList(orgDAO.findAll()));

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            deleteButton.setDisable(sel == null);
        });

        loadTable();
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadTable() {
        table.setItems(FXCollections.observableArrayList(distDAO.findAll()));
    }

    @FXML
    private void filterHandle(ActionEvent event) {
        Organization org = filterOrgCombo.getValue();
        if (org != null) {
            table.setItems(FXCollections.observableArrayList(distDAO.findByOrgId(org.getOrgId())));
        }
    }

    @FXML
    private void showAllHandle(ActionEvent event) {
        filterOrgCombo.setValue(null);
        loadTable();
    }

    @FXML
    private void refreshHandle(ActionEvent event) { loadTable(); }

    @FXML
    private void deleteHandle(ActionEvent event) {
        AidDistribution sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete distribution #" + sel.getDistributionId() + " for " + sel.getFamilyName() + "?");
        if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
            if (distDAO.deleteOne(sel)) {
                loadTable();
                deleteButton.setDisable(true);
            }
        }
    }

    @FXML
    private void backHandle(ActionEvent event) { navigateTo("../views/AdminDashboard.fxml"); }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) menubar.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(AidDistributionController.class.getName()).log(Level.SEVERE, null, ex);
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
}
