package Libs;

import FileSystem.SCI.File;

import java.io.IOException;

public class User {

    static final int bytesUsername = 8;
    static final int bytesPassword = 8;
    static final int bytesFlags = 1;
    static final int bytesTotal = bytesUsername + bytesPassword + bytesFlags;

    public static byte[] newFileNode(String username, String password, Boolean admin) throws Exception {

        if (!username.matches("^[A-Za-z0-9]+$")) {
            throw new Exception("Incorrect username!");
        }
        if (!password.matches("^[A-Za-z0-9]+$")) {
            throw new Exception("Incorrect username!");
        }
        byte[] node;
        node = Bytes.resize(Bytes.toBytes(username), bytesUsername);
        node = Bytes.merge(node, Bytes.resize(Bytes.toBytes(password), bytesPassword));
        node = Bytes.merge(node, Bytes.toBytes(Bytes.toByte(new boolean[]{admin, false})));
        return node;
    }

    protected String username;
    protected String password;
    protected boolean admin;
    protected boolean main;

    private File users;
    private long position;

    protected User(File users, long position) throws IOException {

        byte[] node = users.read(position, bytesTotal);

        int nodePosition = 0;
        username = Bytes.toString(Bytes.dropFreeEnd(Bytes.cut(node, 0, nodePosition + bytesUsername - 1)));

        nodePosition += bytesUsername;
        password = Bytes.toString(Bytes.dropFreeEnd(Bytes.cut(node, nodePosition, nodePosition + bytesPassword - 1)));

        nodePosition += bytesPassword;
        boolean[] properties = Bytes.toBooleans(node[nodePosition]);
        main = properties[1];
        admin = properties[0];

        this.users = users;
        this.position = position;
    }

    public boolean setPassword(String password) throws IOException {

        if (!password.matches("^[A-Za-z0-9]+$")) {
            return false;
        }
        this.password = password;
        users.write(position + bytesUsername, Bytes.resize(Bytes.toBytes(password), 8));
        return true;
    }

    public void setAdmin(boolean admin) throws IOException {

        this.admin = admin;
        users.write(position + bytesUsername + bytesPassword, Bytes.toBytes(Bytes.toByte(new boolean[]{admin, main})));
    }

    public void setMain(boolean main) throws IOException {

        this.main = main;
        users.write(position + bytesUsername + bytesPassword, Bytes.toBytes(Bytes.toByte(new boolean[]{admin, main})));
    }

    public String getUsername() {
        return username;
    }

    public boolean verifyPassword(String password) {
        return password.equals(this.password);
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isMain() {
        return main;
    }

}
