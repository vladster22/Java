package FileSystem.Driver;

import FileSystem.DriveIO;
import Libs.Bytes;

import java.io.IOException;
import java.math.BigDecimal;

public class DriveData {

    protected final DriveIO driveIO;

    private final long startByte;

    public DriveData(DriveIO driveIO) {
        this.driveIO = driveIO;
        startByte = 24L + Long.BYTES * driveIO.clusters;
    }

    public byte[] getCluster(final long clusterId) throws IOException {

        if (clusterId < 0 || clusterId >= driveIO.clusters) {
            throw new IllegalArgumentException("Cluster with a specified Id does not exist.");
        }

        byte[] cluster = new byte[driveIO.clusterSize];
        driveIO.drive.seek(startByte + clusterId * driveIO.clusterSize);
        for (int i = 0; i < cluster.length; i++) {
            cluster[i] = driveIO.drive.readByte();
        }
        return cluster;
    }

    public void setCluster(final long clusterId, byte[] cluster) throws IOException {

        if (clusterId < 0 || clusterId >= driveIO.clusters) {
            throw new IllegalArgumentException("Cluster with a specified Id does not exist.");
        }
        if (cluster.length > driveIO.clusterSize) {
            throw new IllegalArgumentException("Exceeded the size of the cluster.");
        }

        driveIO.drive.seek(startByte + clusterId * driveIO.clusterSize);
        driveIO.drive.write(cluster);
        driveIO.drive.write(new byte[driveIO.clusterSize - cluster.length]);
    }

    public void freeCluster(final long clusterId) throws IOException {

        if (clusterId < 0 || clusterId >= driveIO.clusters) {
            throw new IllegalArgumentException("Cluster with a specified Id does not exist.");
        }

        driveIO.drive.seek(startByte + clusterId * driveIO.clusterSize);
        driveIO.drive.write(new byte[driveIO.clusterSize]);
    }

    public void freeClusters() throws IOException {

        driveIO.drive.seek(startByte);
        for (long i = 0; i < driveIO.clusters * driveIO.clusterSize; i++) {
            driveIO.drive.writeByte(0x00);
        }
    }

}
