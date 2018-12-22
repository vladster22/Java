package FileSystem.VFS;

import FileSystem.DriveIO;
import FileSystem.Driver.DriveBitmap;

import java.io.IOException;

public class Clusters {

    public static final long ListNotCreate = -1L;
    public static final long ClusterNotFind = -1L;

    public static long newList(final DriveIO driveIO, final int clusters) throws IOException {

        if (!driveIO.bitmap.checkAvailableFreeClusters(clusters)) {
            return ListNotCreate;
        }

        long searchFreeClusterSize = 0L;
        final long startClusterId = driveIO.bitmap.getFreeClusterId(searchFreeClusterSize);
        searchFreeClusterSize = startClusterId + 1;
        long currentClusterId = startClusterId;
        driveIO.data.freeCluster(currentClusterId);
        for (int i = 1; i < clusters; i++) {
            long freeClusterId = driveIO.bitmap.getFreeClusterId(searchFreeClusterSize);
            searchFreeClusterSize = freeClusterId + 1;
            driveIO.bitmap.setValue(currentClusterId, freeClusterId);
            currentClusterId = freeClusterId;
            driveIO.data.freeCluster(currentClusterId);
        }
        driveIO.bitmap.setValue(currentClusterId, DriveBitmap.EndCluster);

        return startClusterId;
    }

    public static void deleteList(final DriveIO driveIO, final long address) throws IOException {

        long currentClusterId = address;
        long nextClusterId = driveIO.bitmap.getValue(currentClusterId);
        driveIO.bitmap.setValue(currentClusterId, DriveBitmap.FreeCluster);
        while (nextClusterId != DriveBitmap.EndCluster) {
            currentClusterId = nextClusterId;
            nextClusterId = driveIO.bitmap.getValue(currentClusterId);
            driveIO.bitmap.setValue(currentClusterId, DriveBitmap.FreeCluster);
        }
    }

    private final DriveIO driveIO;

    private final long startClusterId;
    
    private long currentClusterId;
    private int size;

    public Clusters(final DriveIO driveIO, final long address) throws IOException {

        this.driveIO = driveIO;
        this.startClusterId = address;
        this.currentClusterId = address;
        // if you do not convert list
        if (driveIO.bitmap.getValue(address) == DriveBitmap.FreeCluster) {
            driveIO.bitmap.setValue(address, DriveBitmap.EndCluster);
        }
        // find out the size of the list
        size = 0;
        long currentClusterId = address;
        do {
            ++size;
            currentClusterId = driveIO.bitmap.getValue(currentClusterId);
        } while (currentClusterId != DriveBitmap.EndCluster);
    }

    /* size functions */
    public int size() throws IOException {
        return size;
    }

    public boolean resize(final int newSize) throws IOException {

        int currentSize = size;
        if (currentSize < newSize) {
            toEnd();
            long addListStartClusterId = newList(driveIO, newSize - currentSize);
            if (addListStartClusterId == ListNotCreate) {
                return false;
            }
            driveIO.bitmap.setValue(currentClusterId, addListStartClusterId);
        }
        if (currentSize > newSize) {
            toCluster(newSize - 1);
            deleteList(driveIO, driveIO.bitmap.getValue(currentClusterId));
            driveIO.bitmap.setValue(currentClusterId, DriveBitmap.EndCluster);
        }
        size = newSize;
        currentClusterId = startClusterId;
        return true;
    }

    /* navigation block */
    public void toStart() {
        currentClusterId = startClusterId;
    }

    public boolean toNext() throws IOException {

        if (currentClusterId == ClusterNotFind) {
            return false;
        }
        currentClusterId = driveIO.bitmap.getValue(currentClusterId);
        if (currentClusterId == DriveBitmap.EndCluster) {
            currentClusterId = ClusterNotFind;
            return false;
        }
        return true;
    }

    public boolean toCluster(int number) throws IOException {

        int counter = 0;
        currentClusterId = startClusterId;
        while (currentClusterId != DriveBitmap.EndCluster && counter < number) {
            ++counter;
            currentClusterId = driveIO.bitmap.getValue(currentClusterId);
        }
        if (currentClusterId == DriveBitmap.EndCluster) {
            this.currentClusterId = ClusterNotFind;
            return false;
        }
        return true;
    }

    public boolean toEnd() throws IOException {

        currentClusterId = startClusterId;
        long nextClusterId = driveIO.bitmap.getValue(currentClusterId);
        while (nextClusterId != DriveBitmap.EndCluster) {
            currentClusterId = nextClusterId;
            nextClusterId = driveIO.bitmap.getValue(currentClusterId);
        }
        return true;
    }

    /* clusters block */
    public boolean clusterExist() {
        return currentClusterId != ClusterNotFind;
    }

    public byte[] getCluster() throws IOException {
        return currentClusterId == ClusterNotFind ? null : driveIO.data.getCluster(currentClusterId);
    }

    public boolean setCluster(byte[] bytes) throws IOException {

        if (currentClusterId == ClusterNotFind) {
            return false;
        }
        driveIO.data.setCluster(currentClusterId, bytes);
        return true;
    }

}
