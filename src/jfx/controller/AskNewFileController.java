package jfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AskNewFileController {
    @FXML
    private Button validate;
    @FXML
    private TextField month, year;

    private Controller controller;
    private Stage askStage;

    public AskNewFileController(Controller controller) throws IOException {
        this.controller = controller;
        askStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/jfx/fxml/askNewFile.fxml"));
        loader.setController(this);
        Parent rootAsk = loader.load();

        Scene askScene = new Scene(rootAsk, 400,100);
        askStage.setScene(askScene);
        askStage.showAndWait();
    }

    @FXML
    private void onValidate() {
       controller.createNewFile(month.getText(), year.getText());
       askStage.close();
    }
}
