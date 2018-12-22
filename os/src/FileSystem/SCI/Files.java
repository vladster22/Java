package FileSystem.SCI;

import FileSystem.DriveIO;
import FileSystem.VFS.Stream;
import Libs.Bytes;

import java.io.IOException;
import java.math.BigDecimal;

public class Files {

    public static final long AddressNoSet = -1;

    private final DriveIO driveIO;
    private final Stream allocation;

    public long count;
    protected long current;

    public Files(final DriveIO driveIO) throws IOException {

        this.driveIO = driveIO;
        allocation = new Stream(driveIO, 0L);
        count = Bytes.toLong(allocation.read(0L, Long.BYTES));
        current = 0;
    }

    public File getFile(final String name) throws IOException {

        for (int i = 0; i < count; i++) {
            File file = new File(driveIO, allocation, Long.BYTES + i * File.bytesTotal);
            if (name.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }

    public File newFile(final String name) throws IOException {

        if (getFile(name) != null || !driveIO.bitmap.checkAvailableFreeClusters(BigDecimal.valueOf((double)(File.bytesTotal - (Long.BYTES + count * File.bytesTotal) % driveIO.clusterSize) / driveIO.clusterSize).setScale(0, BigDecimal.ROUND_CEILING).intValue() +  1)) {
            return null;
        }
        try {
            allocation.write(Long.BYTES + count * File.bytesTotal, File.newAllocationNode(name, -1));
        } catch (IllegalArgumentException e) {
            return null;
        }
        allocation.write(0, Bytes.toBytes(++count));
        return new File(driveIO, allocation, Long.BYTES + (count - 1) * File.bytesTotal);

    }

    public boolean renameFile(final String oldName,final String newName) throws IOException {
        File file = getFile(oldName);
        if (file == null || getFile(newName) != null) {
            return false;
        }
        file.rename(newName);
        return true;
    }

    public boolean deleteFile(final String name) throws IOException {

        for (int i = 0; i < count; i++) {
            File file = new File(driveIO, allocation, Long.BYTES + i * File.bytesTotal);
            if (name.equals(file.getName())) {
                if (!file.isRemove()) {
                    return false;
                }
                byte[] deleteFileNode = allocation.read(Long.BYTES + i * File.bytesTotal, File.bytesTotal);
                allocation.write(Long.BYTES + i * File.bytesTotal, allocation.read(Long.BYTES + (count - 1) * File.bytesTotal, File.bytesTotal));
                allocation.write(Long.BYTES + (count - 1) * File.bytesTotal, deleteFileNode);
                allocation.write(0, Bytes.toBytes(--count));
                allocation.resize(Long.BYTES + count * File.bytesTotal);
                Stream.deleteStream(driveIO, Bytes.toLong(Bytes.cut(deleteFileNode, File.bytesTotal - File.bytesAddress, File.bytesTotal - 1)));
                current = 0;
                return true;
            }
        }
        return true;
    }

    public void toStart() {
        current = 0;
    }

    public boolean toNext() {
        if (current < count - 1) {
            current++;
            return true;
        }
        return false;
    }

    public File getCurrent() throws IOException {
        return current < count ? new File(driveIO, allocation, Long.BYTES + current * File.bytesTotal) : null;
    }

}
