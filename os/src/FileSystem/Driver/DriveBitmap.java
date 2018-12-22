package FileSystem.Driver;

import FileSystem.DriveIO;

import java.io.IOException;

public class DriveBitmap {

    protected final DriveIO driveIO;

    private final long startByte;

    public DriveBitmap(DriveIO driveIO) {
        this.driveIO = driveIO;
        startByte = 24L;
    }

    public static long FreeCluster = 0L;
    public static long EndCluster = -1L;
    public static long FreeClusterNotExist = -1L;

    public long getValue(final long clusterId) throws IOException {

        if (clusterId < 0 || clusterId >= driveIO.clusters) {
            throw new IllegalArgumentException("Cluster with a specified Id does not exist.");
        }

        driveIO.drive.seek(startByte + clusterId * Long.BYTES);
        return driveIO.drive.readLong();
    }

    public long setValue(final long clusterId, final long value) throws IOException {

        long oldValue = getValue(clusterId);

        if (value >= driveIO.clusters) {
            throw new IllegalArgumentException("Not allowed values greater than or equal to the number of clusters.");
        }

        driveIO.drive.seek(startByte + clusterId * Long.BYTES);
        driveIO.drive.writeLong(value);

        return oldValue;
    }

    public long getFreeClusterId() throws IOException {

        long freeClusterId = 0L;
        driveIO.drive.seek(startByte);
        while (freeClusterId < driveIO.clusters && driveIO.drive.readLong() != FreeCluster) {
            ++freeClusterId;
        }
        return freeClusterId < driveIO.clusters ? freeClusterId : FreeClusterNotExist;
    }

    public long getFreeClusterId(final long afterClusterId) throws IOException {

        if (afterClusterId < 0 || afterClusterId >= driveIO.clusters) {
            throw new IllegalArgumentException("Cluster with a specified Id does not exist.");
        }

        long freeClusterId = afterClusterId;
        driveIO.drive.seek(startByte + afterClusterId * Long.BYTES);
        while (freeClusterId < driveIO.clusters && driveIO.drive.readLong() != FreeCluster) {
            ++freeClusterId;
        }
        return freeClusterId < driveIO.clusters ? freeClusterId : FreeClusterNotExist;
    }

    public long freeClustersCount() throws IOException {

        long freeClusterCount = 0L;
        driveIO.drive.seek(startByte);
        for (int i = 0; i < driveIO.clusters; i++) {
            if (driveIO.drive.readLong() == FreeCluster) {
                ++freeClusterCount;
            }
        }
        return freeClusterCount;
    }

    public boolean checkAvailableFreeClusters(final int clusters) throws IOException {

        if (clusters <= 0) {
            throw new IllegalArgumentException("Incorrect clusters count.");
        }

        int left = clusters;
        driveIO.drive.seek(startByte);
        for (int i = 0; i < driveIO.clusters; i++) {
            if (driveIO.drive.readLong() == FreeCluster) {
                --left;
                if (left == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void freeBitmap() throws IOException {

        driveIO.drive.seek(startByte);
        for (long i = 0; i < driveIO.clusters * Long.BYTES; i++) {
            driveIO.drive.writeByte(0x00);
        }
    }

}
