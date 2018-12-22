package Libs;

import FileSystem.DriveIO;
import FileSystem.SCI.File;
import Shell.Variables;

import java.io.IOException;

abstract public class Users {

    public static User newUser(String username, String password, Boolean admin) throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        if (users == null) {
            users = Variables.systemDrive.files.newFile("users");
            if (users == null) {
                return null;
            }
            users.setRemove(false);
            users.setSystem(true);
            users.setText(false);
            users.setUsername("system");
        }
        byte count = users.read(0, Byte.BYTES)[0];
        for (int i = 0; i < count; i++) {
            User user = new User(users, Byte.BYTES + i * User.bytesTotal);
            if (username.equals(user.getUsername())) {
                return null;
            }
        }
        try {
            if (users.write(Byte.BYTES + count * User.bytesTotal, User.newFileNode(username, password, admin))) {
                users.write(0, Bytes.toBytes(++count));
                return new User(users, Byte.BYTES + (count - 1) * User.bytesTotal);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static User getUser(String username, String password) throws IOException {

        User user = getUser(username);
        return user != null && user.verifyPassword(password) ? user : null;
    }

    public static User getUser(String username) throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        if (users == null) {
            return null;
        }
        byte count = users.read(0, Byte.BYTES)[0];
        for (int i = 0; i < count; i++) {
            User user = new User(users, Byte.BYTES + i * User.bytesTotal);
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public static User[] getUsers() throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        if (users != null) {
            byte count = users.read(0, Byte.BYTES)[0];
            User[] array = new User[count];
            for (int i = 0; i < count; i++) {
                array[i] = new User(users, Byte.BYTES + i * User.bytesTotal);
            }
            return array;
        }
        return null;
    }

    public static byte getCount() throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        if (users == null) {
            return 0;
        }
        return Bytes.toByte(users.read(0, Byte.BYTES));
    }

    public static boolean exist() throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        return users != null && Bytes.toByte(users.read(0, Byte.BYTES)) > 0;
    }

    public static boolean deleteUser(String username) throws IOException {

        File users = Variables.systemDrive.files.getFile("users");
        if (users == null) {
            return false;
        }
        byte count = users.read(0, Byte.BYTES)[0];
        for (int i = 0; i < count; i++) {
            User user = new User(users, Byte.BYTES + i * User.bytesTotal);
            if (username.equals(user.getUsername())) {
                byte[] deleteUserNode = users.read(Byte.BYTES + i * User.bytesTotal, User.bytesTotal);
                users.write(Byte.BYTES + i * User.bytesTotal, users.read(Byte.BYTES + (count - 1) * User.bytesTotal, User.bytesTotal));
                users.write(Byte.BYTES + (count - 1) * User.bytesTotal, deleteUserNode);
                users.write(0, Bytes.toBytes(--count));
                users.resize(Byte.BYTES + count * User.bytesTotal);
                return true;
            }
        }
        return false;
    }

}
