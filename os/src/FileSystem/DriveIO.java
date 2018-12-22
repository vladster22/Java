package FileSystem;

import FileSystem.Driver.DriveBitmap;
import FileSystem.Driver.DriveData;
import FileSystem.SCI.Files;
import FileSystem.VFS.Stream;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DriveIO {

    public static void newDrive(String drive, long bytes, int clusterSize) throws IOException {

        if (drive.length() == 0) {
            throw new IllegalArgumentException("Error in drive name.");
        }
        if (bytes < 0) {
            throw new IllegalArgumentException("The drive must contain at least 800 bytes.");
        }
        if (clusterSize < 8) {
            throw new IllegalArgumentException("The cluster size must be greater than 8.");
        }

        final long clusters = bytes / (clusterSize + Long.BYTES);

        RandomAccessFile newDrive = new RandomAccessFile("hardware/" + drive, "rws");

        newDrive.writeLong(bytes);
        newDrive.writeInt(clusterSize);
        newDrive.writeLong(clusters);

        newDrive.seek(24L + bytes - 1);
        newDrive.write(0x00);
    }

    public final RandomAccessFile drive;

    public final long bytes;
    public final int clusterSize;
    public final long clusters;

    public final DriveBitmap bitmap;
    public final DriveData data;
    public final Files files;

    public DriveIO(String drive) throws IOException {

        if (drive.length() == 0) {
            throw new IllegalArgumentException("Error in drive name.");
        }

        this.drive = new RandomAccessFile("hardware/" + drive, "rws");

        bytes = this.drive.readLong();
        clusterSize = this.drive.readInt();
        clusters = this.drive.readLong();

        bitmap = new DriveBitmap(this);
        data = new DriveData(this);

        files = new Files(this);
    }

    public void fastFormat() throws IOException {
        bitmap.freeBitmap();
    }

    public void fullFormat() throws IOException {
        bitmap.freeBitmap();
        data.freeClusters();
    }

}
