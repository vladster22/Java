package UI.Notepad;

import FileSystem.SCI.File;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Notepad {

    public Notepad(final Stage primaryStage, final File file) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("notepad.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(file.getName() + " - Options");

        stage.setScene(new Scene(root, 300, 295));
        stage.setResizable(false);

        Controller controller = loader.getController();
        if (controller != null) {
            controller.setStage(stage);
            controller.setFile(file);
        }
        stage.showAndWait();
    }
}
