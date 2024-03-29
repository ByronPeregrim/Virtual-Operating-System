import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FakeFileSystem implements Device {

    RandomAccessFile[] array = new RandomAccessFile[10];
    
    FakeFileSystem () {
        
    }

    public void Close(int id) {
        try {
            array[id].close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FakeFileSystem: Close: IOException.");
            System.exit(2);
        }
        array[id] = null;
    }

    public int Open(String string) {
        if (string == null || string == "") {
            try {
                throw new FileNotFoundException(string);
            } catch (FileNotFoundException e) {
                System.exit(0);
                e.printStackTrace();
            }
        }
        RandomAccessFile newRandomAccessFile;
        try {
            newRandomAccessFile = new RandomAccessFile(string, "rw");
            // If file opened is swap file, erase all contents
            if (string.equals("swap.dat")) {
                newRandomAccessFile.setLength(0);
            }
        } catch (IOException e) {
            System.err.println("FakeFileSystem: Open: File: " + string + " not found.");
            newRandomAccessFile = null;
            e.printStackTrace();
            System.exit(1);
        }
        int i = 0;
        for (i = 0; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = newRandomAccessFile;
                break;
            }
        }
        return i;
    }

    public byte[] Read(int id, int size) {
        byte[] byteArray = new byte[size];
        RandomAccessFile current = array[id];
        try {
            current.read(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FakeFileSystem: Read: IOException.");
            System.exit(3);
        }
        return byteArray;
    }

    public void Seek(int id, int to) {
        // Offset file-pointer to second argument
        try {
            array[id].seek((long)to);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FakeFileSystem: Seek: IOException.");
            System.exit(5);
        }
    }

    public int Write(int id, byte[] data) {
        RandomAccessFile current = array[id];
        // If write successful, return 1. Otherwise, return 0.
        try {
            current.write(data);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FakeFileSystem: Write: IOException.");
            System.exit(4);
            return -1;
        }
    }

}
