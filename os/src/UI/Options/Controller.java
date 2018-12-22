package UI.Options;

import FileSystem.SCI.File;
import Libs.User;
import Shell.Variables;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    private File file;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    TextField filename;
    @FXML
    TextField username;
    @FXML
    Label dateCreate;
    @FXML
    Label dateRead;
    @FXML
    Label dateUpdate;
    @FXML
    CheckBox system;
    @FXML
    CheckBox hidden;
    @FXML
    CheckBox remove;
    @FXML
    CheckBox text;
    @FXML
    Button save;
    @FXML
    Label fileSize;

    public void setFile(File file) throws IOException {

        filename.setText(file.getName());
        username.setText(file.getUsername());

        dateCreate.setText(file.getCreate().toString());
        dateRead.setText(file.getRead().toString());
        dateUpdate.setText(file.getUpdate().toString());

        system.setSelected(file.isSystem());
        remove.setSelected(file.isRemove());
        hidden.setSelected(file.isHidden());
        text.setSelected(file.isText());

        if ((Variables.user.isAdmin() || Variables.user.getUsername().equals(file.getUsername())) && !file.isSystem()) {
            remove.setDisable(false);
            hidden.setDisable(false);
            text.setDisable(false);
        }

        save.setDisable(false);

        fileSize.setText(file.size() + " bytes");

        this.file = file;
    }

    public void close() {

        stage.close();
    }

    public void save() throws IOException {

        file.setHidden(hidden.isSelected());
        file.setRemove(remove.isSelected());
        file.setText(text.isSelected());
        close();
    }

}
