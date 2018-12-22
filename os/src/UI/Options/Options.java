package UI.Options;

import FileSystem.SCI.File;
import Libs.User;
import UI.Profile.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Options {

    public Options(final Stage primaryStage, final File file) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("options.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(file.getName() + " - Options");

        stage.setScene(new Scene(root, 375, 310));
        stage.setResizable(false);

        Controller controller = loader.getController();
        if (controller != null) {
            controller.setStage(stage);
            controller.setFile(file);
        }
        stage.showAndWait();
    }
}
