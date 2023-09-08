import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;

public class Kernel implements Device {

    private Scheduler scheduler;
    private VFS VFS = new VFS();

    public Kernel() {
        scheduler = new Scheduler();
    }

    public int CreateProcess(UserlandProcess up, OS.Priority priority, boolean callSleep) {
        return scheduler.CreateProcess(up,priority,callSleep);
    }

    public void Sleep(int milliseconds) {
        scheduler.Sleep(milliseconds);
    }

    public int Open(String string) throws InvalidAlgorithmParameterException, IOException {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        int[] VFS_ID_Array = currentProcess.get_VFS_ID_Array();
        System.out.println(currentProcess + " " + currentProcess.getPriority());
        for (int i = 0; i < VFS_ID_Array.length; i++) {
            if (VFS_ID_Array[i] == -1) {
                // If an empty spot is found, pass open call to VFS
                int VFS_ID = VFS.Open(string);
                // If VFS returns -1, fail
                if (VFS_ID == -1) {
                    return -1;
                }
                else {
                    VFS_ID_Array[i] = VFS_ID;
                    currentProcess.set_VFS_ID_Array(VFS_ID_Array);
                    // Returns the location of the VFS_ID in the process' array
                    return i;
                }
            }
        }
        // If array is full, fail
        return -1;
    }

    public void Close(int id) {
        // Retrieve VFS_ID from Kerneland.
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        int[] VFS_ID_Array = currentProcess.get_VFS_ID_Array();
        int VFS_ID = VFS_ID_Array[id];
        // Pass Kernel call through to VFS
        VFS.Close(VFS_ID);
        VFS_ID_Array[id] = -1;
        currentProcess.set_VFS_ID_Array(VFS_ID_Array);
    }

    public byte[] Read(int id, int size) throws IOException {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        int[] VFS_ID_Array = currentProcess.get_VFS_ID_Array();
        int VFS_ID = VFS_ID_Array[id];
        return VFS.Read(VFS_ID, size);
    }

    public int Write(int id, byte[] data) {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        int[] VFS_ID_Array = currentProcess.get_VFS_ID_Array();
        int VFS_ID = VFS_ID_Array[id];
        return VFS.Write(VFS_ID, data);
    }

    public void Seek(int id, int to) throws IOException {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        int[] VFS_ID_Array = currentProcess.get_VFS_ID_Array();
        int VFS_ID = VFS_ID_Array[id];
        VFS.Seek(VFS_ID, to);
    }
}
