package FileSystem.SCI;

import FileSystem.DriveIO;
import FileSystem.VFS.Stream;
import Libs.Bytes;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import jdk.nashorn.internal.runtime.regexp.joni.Matcher;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

public class File {

    public static int bytesName      = Byte.BYTES * 8;
    public static int bytesAttribute = Byte.BYTES;
    public static int bytesCreate    = Long.BYTES;
    public static int bytesRead      = Long.BYTES;
    public static int bytesUpdate    = Long.BYTES;
    public static int bytesUsername  = Byte.BYTES * 8;
    public static int bytesAddress   = Long.BYTES;

    public static int bytesTotal     = Byte.BYTES * 16 + Byte.BYTES + Long.BYTES * 4;

    public static byte[] newAllocationNode(final String name, final long address) {

        if (!name.matches("[A-Za-z0-9]{1,8}")) {
            throw new IllegalArgumentException("Incorrect name.");
        }
        Date today = new Date();
        byte[] node;
        node = Bytes.resize(Bytes.toBytes(name), bytesName);
        node = Bytes.push(node, (byte)0x00);
        node = Bytes.merge(node, Bytes.toBytes(today));
        node = Bytes.merge(node, Bytes.toBytes(today));
        node = Bytes.merge(node, Bytes.toBytes(today));
        node = Bytes.merge(node, new byte[8]);
        node = Bytes.merge(node, Bytes.toBytes(address));

        return node;
    }

    private String name;

    /* attributes */
    private boolean remove;
    private boolean system;
    private boolean hidden;
    private boolean text;

    /* timestamps */
    private Date create;
    private Date read;
    private Date update;

    /* options */
    private String username;

    /* system options */
    private final Stream allocation;
    private final long nodeAddress;
    private final Stream stream;

    public File (DriveIO driveIO, Stream allocation, long nodeAddress) throws IOException {

        this.allocation = allocation;
        this.nodeAddress = nodeAddress;

        byte[] node = allocation.read(nodeAddress, bytesTotal);

        int position = 0;

        name = Bytes.toString(Bytes.dropFreeEnd(Bytes.cut(node, position, bytesName - 1)));

        position += bytesName;
        boolean[] attributes = Bytes.toBooleans(node[position]);
        remove = attributes[0];
        system = attributes[1];
        hidden = attributes[2];
        text   = attributes[3];

        position += bytesAttribute;
        create = Bytes.toDate(Bytes.cut(node, position, position + bytesCreate - 1));

        position += bytesCreate;
        read   = Bytes.toDate(Bytes.cut(node, position, position + bytesRead - 1));

        position += bytesRead;
        update = Bytes.toDate(Bytes.cut(node, position, position + bytesUpdate - 1));

        position += bytesUpdate;
        username = Bytes.toString(Bytes.dropFreeEnd(Bytes.cut(node, position, position + bytesUsername - 1)));

        position += bytesUsername;
        long address = Bytes.toLong(Bytes.cut(node, position, position + bytesAddress - 1));
        if (address == Files.AddressNoSet) {
            address = driveIO.bitmap.getFreeClusterId();
            driveIO.data.freeCluster(address);
            allocation.write(nodeAddress + position, Bytes.toBytes(address));
        }
        stream = new Stream(driveIO, address);
    }

    public String getName() {
        return name;
    }

    protected void rename(String name) throws IOException {

        if (!name.matches("[A-Za-z0-9]{1,8}")) {
            throw new IllegalArgumentException("Incorrect name.");
        }

        this.name = name;
        allocation.write(nodeAddress, Bytes.resize(Bytes.toBytes(name), bytesName));
        updateWrite();
    }

    public boolean isRemove() {
        return remove;
    }

    public boolean isSystem() {
        return system;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isText() {
        return text;
    }

    private void setAttribute() throws IOException {
        allocation.write(nodeAddress + bytesName + bytesAttribute - 1, Bytes.toBytes(Bytes.toByte(new boolean[]{remove, system, hidden, text})));
    }

    public void setRemove(boolean remove) throws IOException {
        this.remove = remove;
        setAttribute();
    }

    public void setSystem(boolean system) throws IOException {
        this.system = system;
        setAttribute();
    }

    public void setHidden(boolean hidden) throws IOException {
        this.hidden = hidden;
        setAttribute();
    }

    public void setText(boolean text) throws IOException {
        this.text = text;
        setAttribute();
    }

    public Date getCreate() {
        return create;
    }

    public Date getRead() {
        return read;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUsername(String username) throws IOException {

        if (!name.matches("[A-Za-z0-9]{1,8}")) {
            throw new IllegalArgumentException("Incorrect username.");
        }
        this.username = username;
        allocation.write(nodeAddress + bytesName + bytesAttribute + bytesCreate + bytesRead + bytesUpdate, Bytes.resize(Bytes.toBytes(username), bytesUsername));
    }

    public String getUsername() {
        return username;
    }

    private void updateRead() throws IOException {
        allocation.write(nodeAddress + bytesName + bytesAttribute, Bytes.toBytes(new Date().getTime()));
    }

    private void updateWrite() throws IOException {
        allocation.write(nodeAddress + bytesName + bytesAttribute, Bytes.toBytes(new Date().getTime()));
        allocation.write(nodeAddress + bytesName + bytesAttribute + bytesRead, Bytes.toBytes(update.getTime()));
    }

    public byte[] read() throws IOException {
        updateRead();
        return stream.read();
    }

    public byte[] read(final long position, final int bytesCount) throws IOException {
        updateRead();
        return stream.read(position, bytesCount);
    }

    public boolean write(final byte[] bytes) throws IOException {
        updateWrite();
        return stream.write(bytes);
    }

    public boolean write(final long position, final byte[] bytes) throws IOException {
        updateWrite();
        return stream.write(position, bytes);
    }

    public boolean resize(final long size) throws IOException {
        return stream.resize(size);
    }

    public long size() throws IOException {
        return stream.bytes();
    }
}
