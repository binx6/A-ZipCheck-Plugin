package me.zipcheck.plugin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class zipcenop {
    // 修复了原项目ZipCenOp在Android上无法使用的问题且只保留“r”参数效果
    static int[] cenFlag = new int[]{80, 75, 1, 2};
    static byte[] cenNotEncryptedFlag = new byte[]{0, 8};

    public static String repair(String file) throws IOException {
        File zip = new File(file);
        long length = zip.length();

        try {
        RandomAccessFile randomAccessFile = new RandomAccessFile(zip, "rw");
            MappedByteBuffer buffer = (randomAccessFile).getChannel()
            .map(FileChannel.MapMode.READ_WRITE, 0, length);
            // NOTE: 在android.jar的MappedByteBuffer里根本没有put这个method，故用ByteBuffer，因为MappedByteBuffer继承ByteBuffer。Google你怎么这么坏.
            ByteBuffer buff = buffer;

            // 遍历查找
            for (int position = 0; position < length; position++) {
                for (int offset = 0; offset < 4 && position + offset < length && buff.get(position + offset) == cenFlag[offset]; offset++) {
                    if (offset == 3) {
                        // NOTE: 在Android上无论MappedByteBuffer还是ByteBuffer都没有I[B这个方法签名，故已注释。
                        // buff.put(position + 8, cenNotEncryptedFlag);
                        // 修复标志
                        for (int i = 0; i < cenNotEncryptedFlag.length; i++) {
                            buff.put(position + 8 + i, cenNotEncryptedFlag[i]);
                        }
                        position += 10;
                    }
                }
            }
        } catch (IOException e) {
            return e.toString();
        }
        return "";
    }
}