package UI.Profile;

import Libs.User;
import Shell.Variables;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    private User user;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {

        stage.close();
    }

    @FXML
    TextField username;
    @FXML
    PasswordField password;
    @FXML
    CheckBox main;
    @FXML
    CheckBox admin;
    @FXML
    Button save;

    public void setUser(final User user) {

        username.setText(user.getUsername());
        password.setText(user.getPassword());
        admin.setSelected(user.isAdmin());
        main.setSelected(user.isMain());

        if (Variables.user.isAdmin() || Variables.user.getUsername().equals(user.getUsername())) {
            password.setDisable(false);
            save.setDisable(false);
            admin.setDisable(!Variables.user.isAdmin() || user.isMain());
        }

        this.user = user;
    }

    public void save() throws IOException {

        if (!user.setPassword(password.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong format of the password.");
            alert.showAndWait();
        } else {
            close();
        }
        user.setAdmin(admin.isSelected());

    }

}
