import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;

public class Main {

    private static String srcFile;
    private static String dstFile;
    private static int bufSize;

    public static void main(String args[]) {
        try {
            for (int i = 0; i < args.length; i++) {
                String key = args[i].split("=")[0];
                String value = args[i].split("=")[1];
                if (key.compareTo("--srcFile") == 0) {
                    srcFile = value;
                }
                if (key.compareTo("--dstFile") == 0) {
                    dstFile = value;
                }
                if (key.compareTo("--bufSize") == 0) {
                    bufSize = Integer.parseInt(value);
                }
                System.out.println();
                //System.out.println(args[i].split("=")[0]);
            }
        } catch (Exception e) {
            System.out.println("Wrong args");
        }
        copy();
    }

    public static void copy() {
        int count;
        int offset = 0;
        Path filePath = null;
        try {
            filePath = Paths.get(srcFile);
        } catch (InvalidPathException e) {
            System.out.println("Path error " + e);
        }

        try (SeekableByteChannel inChan = Files.newByteChannel(filePath)) {
            ByteBuffer mBuf = ByteBuffer.allocate(bufSize);
            do {
                count = inChan.read(mBuf);
                if (count != -1) {
                    mBuf.compact();
                    mBuf.rewind();
                    write(mBuf, offset, count);
                    offset += bufSize;
                    //System.out.print(100*offset/Files.size(filePath));
                    //System.out.println("%");
                }
                mBuf.clear();
            } while (count != -1);
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }

    public static void write(ByteBuffer mBuf, int offset, int count) {
        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get(dstFile), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            fileChannel.write(mBuf,offset);
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }
}
