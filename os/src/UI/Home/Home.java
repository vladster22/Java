package UI.Home;

import Shell.Variables;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Home {

    public Home(final Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("home.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 750, 400));
        Controller controller = loader.getController();
        if (controller != null) {
            controller.setStage(primaryStage);
        }
        primaryStage.show();
    }

}
