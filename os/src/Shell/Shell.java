package Shell;

import FileSystem.SCI.File;
import Libs.Bytes;
import Libs.User;
import Libs.Users;
import Tasks.Manager;
import UI.Home.Controller;
import UI.Notepad.Notepad;
import UI.Options.Options;
import UI.Profile.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

abstract public class Shell {

    public static String handle(final ArrayList<String> params) throws IOException {

        if (Variables.user == null) {
            return loginHandle(params);
        }
        switch (params.get(0)) {
            case "user":
                return userHandle(params);
            case "file":
                return fileHandle(params);
            case "logout":
                switch (params.size()) {
                    case 1:
                        Variables.user = null;
                        return "Logout successful.";
                }
                return "Wrong command format.";
            case "task":
                return taskHandle(params);
        }
        return "The command is not recognized.";
    }

    public static String loginHandle(final ArrayList<String> params) throws IOException {

        int paramsCount = params.size();
        if (!Users.exist()) {
            switch (params.get(0)) {
                case "user":
                    if (paramsCount == 1) {
                        return "Available commands:\n"+
                                "  Commands:\n" +
                                "   - new [username] [password]";
                    }
                    switch (params.get(1)) {
                        case "new":
                            switch (paramsCount) {
                                case 2:
                                    return "Command format:" +
                                            " - new [username] [password]";
                                case 4:
                                    Variables.user = Users.newUser(params.get(2), params.get(3), true);
                                    if (Variables.user != null) {
                                        Variables.user.setMain(true);
                                        return "Main user create successful.";
                                    }
                                    return "Main user create failed.";
                                default:
                                    return "Wrong command format.\n" +
                                            "  Command format:\n" +
                                            "   - new [username] [password]";
                            }
                    }
                    return "The command is not recognized.\n" +
                            "  Available command:\n" +
                            "   - new [username] [password]";
            }
            return "The command is not recognized.\n" +
                    "  Available command:\n" +
                    "   - user new [username] [password]";
        }
        switch (params.get(0)) {
            case "login":
                switch (paramsCount) {
                    case 1:
                        return "Command format:\n" +
                                " - login [username] [password]";
                    case 3:
                        Variables.user = Users.getUser(params.get(1), params.get(2));
                        return Variables.user == null ? "Login failed." : "Login successful.";
                    default:
                        return "Wrong command format.\n" +
                                "  Command format:\n" +
                                "   - login [username] [password]";
                }
            case "info":
                return "Available commands:\n"+
                        " - login [username] [password]\n" +
                        " - info ";
        }
        return "The command is not recognized.\n" +
                "  Available command:\n" +
                "   - login [username] [password]\n" +
                "   - info ";
    }

    public static String userHandle(final ArrayList<String> params) throws IOException {

        int paramsCount = params.size();

        if (paramsCount > 1) {
            switch (params.get(1)) {
                case "new":
                    switch (paramsCount) {
                        case 4:
                            return Variables.user.isAdmin()
                                    ? Users.newUser(params.get(2), params.get(3), false) != null
                                    ? "Create user successful."
                                    : "Create user failed."
                                    : "There are no rights.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - new [username] [password]";
                case "delete":
                    switch (paramsCount) {
                        case 3:
                            return Variables.user.isAdmin()
                                    ? Users.deleteUser(params.get(2))
                                    ? "Delete user successful."
                                    : "Delete user failed."
                                    : "There are no rights.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - delete [username]";
                case "profile":
                    switch (paramsCount) {
                        case 2:
                            new Profile(Variables.primaryStage, Variables.user);
                            return "Profile successful opening.";
                        case 3:
                            User user = Users.getUser(params.get(2));
                            if (user != null) {
                                new Profile(Variables.primaryStage, user);
                                return "Profile successful opening.";
                            }
                            return "Profile failed opening.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - profile ? / [username]";
            }
            return "The command is not recognized.\n" +
                    "  Available command:\n" +
                    "   - profile ? / [username]\n" +
                    "   - new [username] [password]\n" +
                    "   - delete [username]";
        }
        return "Wrong command format.\n" +
                "  Available command:\n" +
                "   - profile ? / [username]\n" +
                "   - new [username] [password]\n" +
                "   - delete [username]";
    }

    public static String fileHandle(final ArrayList<String> params) throws IOException {

        int paramsCount = params.size();

        if (paramsCount > 1) {
            switch (params.get(1)) {
                case "new":
                    switch (paramsCount) {
                        case 3:
                            File file = Variables.systemDrive.files.newFile(params.get(2));
                            if (file == null) {
                                return "File creation failed!";
                            }
                            file.setRemove(true);
                            file.setText(true);
                            file.setUsername(Variables.user.getUsername());
                            return "File creation successfully!";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - new [filename]";
                case "delete":
                    switch (paramsCount) {
                        case 3:
                            File file = Variables.systemDrive.files.getFile(params.get(2));
                            return file == null
                                    ? "File not exist."
                                    : file.isRemove()
                                    ? Variables.user.getUsername().equals(file.getUsername()) || Variables.user.isAdmin()
                                    ? Variables.systemDrive.files.deleteFile(params.get(2))
                                    ? "Delete file successful."
                                    : "Delete file failed."
                                    : "You do not have access to the file."
                                    : "Deleting a file is prohibited.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - delete [filename]";
                case "rename":
                    switch (paramsCount) {
                        case 4:
                            File file = Variables.systemDrive.files.getFile(params.get(2));
                            if (file == null) {
                                return "File not exist.";
                            }
                            return Variables.systemDrive.files.renameFile(params.get(2), params.get(3))
                                    ? "The file was renamed successfully."
                                    : "Renaming a file failed.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - rename [old filename] [new filename]";
                case "copy":
                    switch (paramsCount) {
                        case 4:
                            File from = Variables.systemDrive.files.getFile(params.get(2));
                            if (from == null) {
                                return "File not exist.";
                            }
                            if (Variables.user.getUsername().equals(from.getUsername()) || Variables.user.isAdmin()) {
                                File where = Variables.systemDrive.files.newFile(params.get(3));
                                if (where == null) {
                                    return "File copy already exists. Copying is not possible.";
                                }
                                if (where.write(from.read())) {
                                    where.setRemove(true);
                                    where.setText(from.isText());
                                    where.setUsername(Variables.user.getUsername());
                                    return "Copying completed successfully.";
                                }
                                Variables.systemDrive.files.deleteFile(where.getName());
                                return "To copy is not enough space.";
                            }
                            return "You do not have access to the file.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - copy [from filename] [where filename]";
                case "open":
                    switch (paramsCount) {
                        case 3:
                            File file = Variables.systemDrive.files.getFile(params.get(2));
                            if (file != null) {
                                if (!file.isText()) {
                                    return "File not support.";
                                }
                                if (Variables.user.isAdmin() || Variables.user.getUsername().equals(file.getUsername())) {
                                    new Notepad(Variables.primaryStage, file);
                                    return "Successful opening.";
                                }
                                return "There are no rights.";
                            }
                            return "Failed opening";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - open [filename]";
                case "options":
                    switch (paramsCount) {
                        case 3:
                            File file = Variables.systemDrive.files.getFile(params.get(2));
                            if (file != null) {
                                new Options(Variables.primaryStage, file);
                                return "Options successful opening.";
                            }
                            return "Options failed opening.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - options [filename]";
                case "run":
                    switch (paramsCount) {
                        case 3:
                            File file = Variables.systemDrive.files.getFile(params.get(2));
                            if (file != null) {
                                String fileContent = Bytes.toString(file.read());
                                String[] commands = fileContent.split("[;\n]+[\\s]?+");
                                String answer = "--------- start running file ---------";
                                for (String command : commands) {
                                    answer += "\ncommand :" + command + "\n" + "answer :" + handle(new ArrayList<>(Arrays.asList(command.split("[\\s]+"))));
                                }
                                return answer + "\n--------- End running file ---------";
                            }
                            return "Failed run start.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - run [filename]";
            }
            return "The command is not recognized.\n" +
                    "  Available command:\n" +
                    "   - new [filename]\n"  +
                    "   - delete [username]\n" +
                    "   - rename [old filename] [new filename]\n" +
                    "   - copy [from filename] [where filename]\n" +
                    "   - options [filename]\n" +
                    "   - open [filename]\n" +
                    "   - run [filename]";
        }
        return "Wrong command format.\n" +
                "  Available command:\n" +
                "   - new [filename]\n"  +
                "   - delete [username]\n" +
                "   - rename [old filename] [new filename]\n" +
                "   - copy [from filename] [where filename]\n" +
                "   - options [filename]\n" +
                "   - open [filename]";
    }

    public static Manager manager;

    public static void managerInit(Controller controller) {
        manager = new Manager(controller);
    }

    public static void managerEnd() {
        manager.close();
    }

    public static String taskHandle(final ArrayList<String> params) {

        int paramsCount = params.size();

        if (paramsCount > 2) {
            switch (params.get(1)) {
                case "new":
                    switch (paramsCount) {
                        case 5:
                            try {
                                return manager.newProcess(params.get(2), Byte.parseByte(params.get(3)), Integer.parseInt(params.get(4)))
                                        ? "Task create successful."
                                        : "Task create fail.";
                            } catch (NumberFormatException e) {
                                return "Task create fail. Incorrect parameter.";
                            }
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - new [name] [priority] [launches]";
                case "kill":
                    switch (paramsCount) {
                        case 3:
                            return manager.killProcessRights(params.get(2)) ?
                                    manager.killProcess(params.get(2))
                                    ? "Task killing successful."
                                    : "Task killing fail."
                                    : "Not enough rights.";
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - kill [name]";
                case "priority":
                    switch (paramsCount) {
                        case 4:
                            try {
                            return manager.newPriority(params.get(2), Byte.parseByte(params.get(3)))
                                    ? "Task set new priority successful."
                                    : "Task set new priority fail.";
                            } catch (NumberFormatException e) {
                                return "Task set new priority fail. Incorrect parameter.";
                            }
                    }
                    return "Wrong command format.\n" +
                            "  Command format:\n" +
                            "   - kill [name]";
            }
            return "The command is not recognized.\n" +
                    "  Available command:\n" +
                    "   - new [name] [priority] [launches]\n"  +
                    "   - kill [name]\n" +
                    "   - priority [name] [priority]";
        }
        return "Wrong command format.\n" +
                "  Available command:\n" +
                "   - new [name] [priority] [launches] \n"  +
                "   - kill [name]\n" +
                "   - priority [name] [priority]";
    }

}
