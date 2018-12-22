package UI.Notepad;

import FileSystem.SCI.File;
import Libs.Bytes;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    private File file;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        stage.close();
    }

    @FXML
    TextArea content;

    public void setFile(File file) throws IOException {
        content.setText(Bytes.toString(file.read()));
        this.file = file;
    }

    public void save() throws IOException {
        file.write(Bytes.toBytes(content.getText()));
    }

}
