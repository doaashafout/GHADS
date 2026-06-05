package controllers;

import dao.AidDistributionDAO;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.AidDistribution;
import models.Family;
import models.User;
import services.DistributionService;
import app.Main;
import config.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

public class AidDistributionFormController implements Initializable {

    @FXML private Label vulnerabilityLabel;
    @FXML private ComboBox<Family> familyCombo;
    @FXML private ComboBox<String> aidTypeCombo;
    @FXML private DatePicker distDatePicker;
    @FXML private Button distributeButton;
    @FXML private Button resetButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private ListView<Family> vulnerableListView;
    @FXML private ListView<Family> notServedListView;
    @FXML private Label vulnerableCount;
    @FXML private Label notServedCount;
    @FXML private MenuBar menubar;
    @FXML private MenuItem exit;
    @FXML private MenuItem about;
    @FXML private ToggleGroup tg;
    @FXML private ToggleGroup tg2;
    @FXML private TableView<AidDistribution> distTable;
    @FXML private TableColumn<AidDistribution, String> distFamilyTC;
    @FXML private TableColumn<AidDistribution, String> distVulnTC;
    @FXML private TableColumn<AidDistribution, String> distAidTypeTC;
    @FXML private TableColumn<AidDistribution, String> distDateTC;

    private FamilyDAO familyDAO;
    private AidDistributionDAO distDAO;
    private DistributionService distService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        familyDAO = new FamilyDAO();
        distDAO = new AidDistributionDAO();
        distService = new DistributionService();

        aidTypeCombo.getItems().addAll("Food", "Water", "Medical", "Clothing", "Shelter", "Cash", "Hygiene Kit");
        distDatePicker.setValue(LocalDate.now());
        familyCombo.setItems(FXCollections.observableArrayList(familyDAO.findAll()));
        familyCombo.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                String vuln = val.getVulnerabilityLevel();
                vulnerabilityLabel.setText("Vulnerability: " + vuln);
                vulnerabilityLabel.setStyle(vuln.equals("HIGH") ? "-fx-text-fill: #c0392b; -fx-font-weight: bold;"
                        : vuln.equals("MEDIUM") ? "-fx-text-fill: #e67e22; -fx-font-weight: bold;"
                        : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                vulnerabilityLabel.setText("");
            }
        });

        distFamilyTC.setCellValueFactory(new PropertyValueFactory<>("familyName"));
        distVulnTC.setCellValueFactory(new PropertyValueFactory<>("familyVulnerability"));
        distAidTypeTC.setCellValueFactory(new PropertyValueFactory<>("aidType"));
        distDateTC.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));

        loadVulnerableList();
        loadNotServedList();
        loadOrgDistributions();
        vulnerableListView.setOnMouseClicked(e -> {
            Family f = vulnerableListView.getSelectionModel().getSelectedItem();
            if (f != null) familyCombo.setValue(f);
        });
        notServedListView.setOnMouseClicked(e -> {
            Family f = notServedListView.getSelectionModel().getSelectedItem();
            if (f != null) familyCombo.setValue(f);
        });
        Main.setupFontControls(tg, tg2, menubar);
    }

    private void loadVulnerableList() {
        vulnerableListView.setItems(FXCollections.observableArrayList(familyDAO.findByVulnerabilityPriority()));
        vulnerableListView.setCellFactory(lv -> new ListCell<Family>() {
            @Override
            protected void updateItem(Family f, boolean empty) {
                super.updateItem(f, empty);
                setText(f == null || empty ? null : f.getHouseholdName() + " [" + f.getVulnerabilityLevel() + "]");
            }
        });
        vulnerableCount.setText(vulnerableListView.getItems().size() + " families");
    }

    private void loadNotServedList() {
        notServedListView.setItems(FXCollections.observableArrayList(familyDAO.findNotServed()));
        notServedListView.setCellFactory(lv -> new ListCell<Family>() {
            @Override
            protected void updateItem(Family f, boolean empty) {
                super.updateItem(f, empty);
                setText(f == null || empty ? null : f.getHouseholdName() + " [" + f.getVulnerabilityLevel() + "]");
            }
        });
        notServedCount.setText(notServedListView.getItems().size() + " families");
    }

    private void loadOrgDistributions() {
        int orgId = SessionManager.getInstance().getCurrentUser().getOrgId();
        distTable.setItems(FXCollections.observableArrayList(distDAO.findByOrgId(orgId)));
    }

    @FXML
    private void distributeHandle(ActionEvent event) {
        Family family = familyCombo.getValue();
        String aidType = aidTypeCombo.getValue();
        LocalDate date = distDatePicker.getValue();

        if (family == null || aidType == null || date == null) {
            showError("Validation Error", "Please select a family, aid type, and date.");
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        String dateStr = date.toString();

        String check = distService.checkDuplicate(family.getFamilyId(), aidType, dateStr);
        if (check.startsWith("REJECTED")) {
            String[] parts = check.split(":");
            String msg = "Duplicate aid detected!\n\n"
                       + "Family: " + parts[1] + "\n"
                       + "Vulnerability: " + parts[2] + "\n"
                       + "Previously given by: " + parts[3] + "\n"
                       + "Date: " + parts[4] + "\n\n"
                       + "This family already received this aid type within the last 30 days.";
            showError("Duplicate Alert", msg);
            return;
        }

        AidDistribution ad = new AidDistribution(family.getFamilyId(), currentUser.getOrgId(),
                currentUser.getUserId(), dateStr, aidType);

        if (distService.recordDistribution(ad)) {
            showInfo("Success", "Aid distribution recorded successfully.");
            loadNotServedList();
            loadVulnerableList();
            loadOrgDistributions();
            resetHandle(event);
        } else {
            showError("Error", "Could not record distribution.");
        }
    }

    @FXML
    private void resetHandle(ActionEvent event) {
        familyCombo.setValue(null);
        aidTypeCombo.setValue(null);
        distDatePicker.setValue(LocalDate.now());
        vulnerabilityLabel.setText("");
    }

    @FXML
    private void refreshHandle(ActionEvent event) {
        familyCombo.setItems(FXCollections.observableArrayList(familyDAO.findAll()));
        loadVulnerableList();
        loadNotServedList();
        loadOrgDistributions();
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
            Logger.getLogger(AidDistributionFormController.class.getName()).log(Level.SEVERE, null, ex);
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
