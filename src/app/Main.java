package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../views/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("GHADS - Gaza Humanitarian Aid Distribution System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setupFontControls(ToggleGroup fontSizeGroup, ToggleGroup fontFamilyGroup, MenuBar menubar) {
        fontSizeGroup.selectedToggleProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                int size = 14;
                switch (((RadioMenuItem) sel).getText()) {
                    case "Small": size = 12; break;
                    case "Large": size = 18; break;
                }
                Scene scene = menubar.getScene();
                if (scene != null)
                    scene.getRoot().setStyle("-fx-font-size: " + size + "px;");
            }
        });

        fontFamilyGroup.selectedToggleProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                String family = ((RadioMenuItem) sel).getText();
                Scene scene = menubar.getScene();
                if (scene != null) {
                    String current = scene.getRoot().getStyle();
                    if (current == null || current.isEmpty())
                        scene.getRoot().setStyle("-fx-font-family: '" + family + "';");
                    else
                        scene.getRoot().setStyle(current + " -fx-font-family: '" + family + "';");
                }
            }
        });
    }
}
