package jfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import jfx.controller.Controller;
import jfx.model.Payment;

public class Main extends Application {

    private Stage primaryStage;

    /*
    On utilise un observable pour pouvoir toujours synchroniser l'affichage avec les données
     */
    private ObservableList<Payment> paymentData = FXCollections.observableArrayList();

    public ObservableList<Payment> getPaymentData() {
        return paymentData;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/sample.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 400);

        Controller controller = loader.getController();
        controller.setMainApp(this);


        this.primaryStage.setTitle("A déclarer");
        this.primaryStage.setScene(scene);
        this.primaryStage.setMinWidth(1000);
        this.primaryStage.setMinHeight(400);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
