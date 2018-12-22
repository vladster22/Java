package UI.Profile;

import Libs.User;
import Shell.Variables;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Profile {

    public Profile(final Stage primaryStage, final User user) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("profile.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(user.getUsername() + " - Profile");

        stage.setScene(new Scene(root, 275, 165));
        stage.setResizable(false);

        Controller controller = loader.getController();
        if (controller != null) {
            controller.setStage(stage);
            controller.setUser(user);
        }
        stage.showAndWait();
    }

}
