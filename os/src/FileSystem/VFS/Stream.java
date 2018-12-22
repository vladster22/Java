package FileSystem.VFS;

import FileSystem.DriveIO;
import FileSystem.SCI.File;
import FileSystem.VFS.Clusters;
import Libs.Bytes;

import java.io.IOException;
import java.math.BigDecimal;

public class Stream {

    public static void deleteStream(final DriveIO driveIO, final long address) throws IOException {

        Clusters.deleteList(driveIO, address);
    }

    private final DriveIO driveIO;
    private final Clusters clusters;

    public Stream(final DriveIO driveIO, final long address) throws IOException {
        this.driveIO = driveIO;
        clusters = new Clusters(driveIO, address);
    }

    public int size() throws IOException {
        return clusters.size();
    }

    public long bytes() throws IOException {
        return clusters.size() * driveIO.clusterSize;
    }

    public boolean resize(long bytes) throws IOException {
        return clusters.resize(BigDecimal.valueOf((double)(bytes) / driveIO.clusterSize).setScale(0, BigDecimal.ROUND_CEILING).intValue());
    }

    public boolean write(byte[] bytes) throws IOException {

        final int size = BigDecimal.valueOf((double)bytes.length/driveIO.clusterSize).setScale(0, BigDecimal.ROUND_CEILING).intValue();

        if(!clusters.resize(size)) {
            return false;
        }

        for (int i = 0; i < size - 1; i++) {
            clusters.setCluster(Bytes.cut(bytes, i * driveIO.clusterSize, (i + 1) * driveIO.clusterSize - 1));
            clusters.toNext();
        }
        clusters.setCluster(Bytes.cut(bytes, (size - 1) * driveIO.clusterSize, bytes.length - 1));

        return true;
    }

    public byte[] read() throws IOException {

        byte[] bytes = clusters.getCluster();
        while (clusters.toNext()) {
            bytes = Bytes.merge(bytes, clusters.getCluster());
        }
        return Bytes.dropFreeEnd(bytes);
    }

    public boolean write(final long position, byte[] bytes) throws IOException {

        if (position < 0) {
            throw new IllegalArgumentException("Error in start byte.");
        }
        if (bytes.length < 1) {
            throw new IllegalArgumentException("Error in bytes count.");
        }

        final int affectedClustersCount = 1 + BigDecimal.valueOf((double)(bytes.length - driveIO.clusterSize + (int)(position % driveIO.clusterSize))/driveIO.clusterSize).setScale(0, BigDecimal.ROUND_CEILING).intValue();

        int size = (int)(position / driveIO.clusterSize) + affectedClustersCount;
        size = size > clusters.size() ? size : clusters.size();
        if (!clusters.resize(size)) {
            return false;
        }
        clusters.toCluster((int)(position / driveIO.clusterSize));
        clusters.setCluster(Bytes.replace(clusters.getCluster(), (int)(position % driveIO.clusterSize), affectedClustersCount == 1 ? bytes : Bytes.cut(bytes, 0, driveIO.clusterSize - (int)(position % driveIO.clusterSize) - 1)));
        int write = driveIO.clusterSize - (int)(position % driveIO.clusterSize);

        for (int i = 0; i < affectedClustersCount - 2; i++, write += driveIO.clusterSize) {
            clusters.toNext();
            clusters.setCluster(Bytes.cut(bytes, write, write + driveIO.clusterSize - 1));
        }

        if (affectedClustersCount > 1) {
            clusters.toNext();
            clusters.setCluster(Bytes.replace(clusters.getCluster(), 0, Bytes.cut(bytes, write, bytes.length - 1)));
        }

        return true;
    }

    public byte[] read(final long position, final int bytesCount) throws IOException {

        if (position < 0) {
            throw new IllegalArgumentException("Error in start byte.");
        }
        if (bytesCount < 1) {
            throw new IllegalArgumentException("Error in bytes count.");
        }

        if (!clusters.toCluster((int)(position / driveIO.clusterSize))) {
            return new byte[0];
        }
        byte[] bytes = Bytes.cut(
                clusters.getCluster(),
                (int)(position % driveIO.clusterSize),
                ((int)(position % driveIO.clusterSize) + bytesCount < driveIO.clusterSize ? (int)(position % driveIO.clusterSize) + bytesCount : driveIO.clusterSize) - 1
        );
        while (bytes.length + driveIO.clusterSize < bytesCount && clusters.toNext()) {
            bytes = Bytes.merge(bytes, clusters.getCluster());
        }
        if (bytes.length < bytesCount && clusters.toNext()) {
            bytes = Bytes.merge(bytes, Bytes.cut(clusters.getCluster(), 0, bytesCount - bytes.length - 1));
        }
        return bytes;
    }

}
