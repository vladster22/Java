import FileSystem.DriveIO;
import Shell.Variables;
import UI.Home.Home;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class Boot extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        DriveIO.newDrive("system.drive", 3264, 24);
        Variables.systemDrive = new DriveIO("system.drive");

        primaryStage.setTitle("OS");
        Variables.primaryStage = primaryStage;
        new Home(primaryStage);

    }

}
