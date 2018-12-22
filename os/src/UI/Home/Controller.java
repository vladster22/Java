package UI.Home;

import FileSystem.SCI.File;
import Libs.User;
import Libs.Users;
import Shell.Shell;
import Shell.Variables;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    private Stage stage;

    protected void setStage(final Stage stage) {

        Shell.managerInit(this);
        this.stage = stage;
        stage.setOnCloseRequest(we -> Shell.managerEnd());
    }

    public void close() {
        stage.close();
    }

    @FXML
    TextArea answers;
    @FXML
    TextField command;
    @FXML
    ListView<String> files;
    @FXML
    ListView<String> users;
    @FXML
    Label currentProcess;
    @FXML
    ListView<String> processes;

    public void setInfo() throws IOException {

        ArrayList<String> titlesUsers = new ArrayList<>();
        User[] users = Users.getUsers();
        if (users != null) {
            for (User i : users) {
                titlesUsers.add(i.getUsername() + "\t\t" + (i.isMain() ? "M" : "") + (i.isAdmin() ? "A" : ""));
            }
        }
        ObservableList<String> usersList = FXCollections.observableList(titlesUsers);
        this.users.setItems(usersList);

        ArrayList<String> titlesFiles = new ArrayList<>();
        Variables.systemDrive.files.toStart();
        if (Variables.systemDrive.files.getCurrent() != null) {
            do {
                File current = Variables.systemDrive.files.getCurrent();
                if (!current.isHidden()) {
                    titlesFiles.add(current.getName() + "\t\t" + (current.isSystem() ? "S" : "") + (current.isRemove() ? "R" : "") + (current.isText() ? "T" : ""));
                }
            } while (Variables.systemDrive.files.toNext());
        }
        ObservableList<String> filesList = FXCollections.observableList(titlesFiles);
        this.files.setItems(filesList);
    }

    public void initialize() throws IOException {
        setInfo();
    }

    public void handle() throws IOException {
        String command = this.command.getText();
        if (!command.isEmpty()) {
            this.command.clear();
            ArrayList<String> params = new ArrayList<>(Arrays.asList(command.split("[\\s]+")));
            switch (command) {
                case "cls":
                    answers.clear();
                    break;
                case "close":
                    close();
                    break;
                default:
                    String user = Variables.user == null ? "" : " > (" + Variables.user.getUsername() + ")";
                    answers.appendText(user + " > " + command + "\n" + Shell.handle(params) + "\n");
                    break;
            }
            setInfo();
        }
    }

    public void showProcess(final String current, final ArrayList<String> titlesProcesses) {
        Platform.runLater(() -> {
            currentProcess.setText(current);
            processes.setItems(FXCollections.observableList(titlesProcesses));
        });
    }


}