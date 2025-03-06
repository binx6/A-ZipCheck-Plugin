import android.support.annotation.NonNull;

import java.io.*;
import java.lang.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.concurrent.*;

import bin.mt.plugin.api.translation.BaseTranslationEngine;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import me.zipcheck.plugin.zipcenop;

public class analyze extends BaseTranslationEngine {
    private static final String[] units = new String[]{"B","KiB","MiB","GiB","TiB"};
    // 没有zip文件注释时候的目录结束符的偏移量
    private static final int RawEndOffset = 22;
    // 0x06054b50占4个字节
    private static final int endOfDirLength = 4;
    // 目录结束标识0x06054b50的小端读取方式
    private static final byte[] endOfDir = new byte[]{0x50, 0x4B, 0x05, 0x06};
    // 允许的最大文件数量
    private static final long MAX_FILE_COUNT = 20000L;
    // 允许的最大文件大小
    private static final long MAX_TOTAL_FILE_SIZE = 1L * 1024L * 1024L * 1024L;
    public analyze() {
        super(new ConfigurationBuilder()
                .setForceNotToSkipTranslated(true)
                .build());
    }

    @NonNull
    @Override
    public String name() {
        return "Zip完整性校验";
    }

    @NonNull
    @Override
    public List<String> loadSourceLanguages() {
        return Collections.singletonList("src");
    }

    @NonNull
    @Override
    public List<String> loadTargetLanguages(String sourceLanguage) {
        return Arrays.asList("badcheck", "rwcheck", "deepcheck", "crccheck", "commoncheck", "bombcheck", "adlercalc", "fakefixer");
    }

    @NonNull
    @Override
    public String getLanguageDisplayName(String language) {
        switch (language) {
            case "src":
                return "快照";
            case "badcheck":
                return "损坏检测";
            case "rwcheck":
                return "读取测试";
            case "deepcheck":
                return "深度探测";
            case "crccheck":
                return "CRC32校验测试";
            case "commoncheck":
                return "整体标基测试";
            case "bombcheck":
                return "Bomb检测";
            case "adlercalc":
                return "Adler32计算";
            case "fakefixer":
                return "ZIP伪加密修复";
        }
        return "???";
    }

    @NonNull
    public static String fileSizeConvert(long size){
    	if (size == 0) {
    		return "0B";
    	}
    	Integer digitGroups = (int) (Math.log(size) / Math.log(1024));
    	digitGroups = digitGroups > 4 ? 4 : digitGroups;
    	return new DecimalFormat("#,##0.##")
    	.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @SuppressWarnings("unchecked")
    public String badCheck(String isBad) {
    String zipFilePath = isBad;
    List<String> results = new ArrayList<>();
    // long totalFileSize = 0;
    try {
    // NOTE: 此处不用ZipFile是因为不需要那么严格的判断，可最大程度的获取内部文件信息。
    FileInputStream fis = new FileInputStream(zipFilePath);
        java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(fis);

            java.util.zip.ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {            
            // NOTE: 下方注释为条目测试，由于比较耗时故不启用。
            // String result = entry.getName();
            long size = entry.getSize();
                // 处理每个文件并跳过文件夹
                if (!entry.isDirectory()) {
                    // 读取文件内容
                    byte[] buffer = new byte[10240];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                    // totalFileSize += len;
                    }
                } else {
                    continue;
                }
                // NOTE: 添加条目。
                // results.add(result);
                /*
                * 初期测试流所用，不用管它
                * String formattedSize = fileSizeConvert(totalFileSize);
                * results.add(formattedSize);
                */
            if (size < 0) {
                // -1 表示大小未知（可能因 ZIP_STORED 未存储大小信息）
                return "异常条目: " + entry.getName() + " 缺少大小信息";
            } else {
                results.add(fileSizeConvert(size));
            }
                zis.closeEntry();
            }
        } catch (IOException | IllegalArgumentException e) {
            return "无法打开ZIP文件，原因: \n" + e.toString();
        }
        // NOTE: 若启用可获取ZIP内部除文件夹外所有文件大小
        // return String.join(",\n", results);
        return "未检测到损坏。";
    }

    @SuppressWarnings("unchecked")
    protected String various(String rwcheck) {
    String zipFilePath = rwcheck;
    java.util.zip.ZipFile zipFile = null;
    try {
        zipFile = new java.util.zip.ZipFile(zipFilePath);
        Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.entries();
        StringBuilder contentBuilder = new StringBuilder();

        while (entries.hasMoreElements()) {
            java.util.zip.ZipEntry entry = entries.nextElement();
            if (entry.isDirectory() || entry.getName().endsWith("/")) {
                continue; // 跳过目录
            }

            InputStream is = null;
            BufferedReader reader = null;
            try {
                is = zipFile.getInputStream(entry);
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {

                }
            } catch (Exception e) {
                return "读取文件内容失败：" + e.toString();
            } finally {
                if (reader != null) try { reader.close(); } catch (IOException ignored) {}
                if (is != null) try { is.close(); } catch (IOException ignored) {}
            }
        }
        return "读取成功！";
    } catch (IOException e) {
        return "无法读取ZIP文件，原因: \n" + e.toString();
    } finally {
        if (zipFile != null) {
            try { zipFile.close(); } catch (IOException ignored) {}
        }
    }
    }

    public boolean isZipValid(String zipFilePath) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            // 初步
            if (!zipFile.isValidZipFile()) {
                return false;
            }
            // 深度
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();            
            for (FileHeader header : fileHeaders) {
                try {
                ZipInputStream zis = zipFile.getInputStream(header);
                    byte[] buffer = new byte[8192];
                    while (zis.read(buffer) != -1) {

                    }
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        } catch (ZipException e) {
            return false;
        }
    }

    public static boolean isZipEncrypted(String zipFilePath) throws IOException {
    try {
        ZipFile zipFile = new ZipFile(zipFilePath);
        
        // 检查整个文件是否加密
        if (zipFile.isEncrypted()) {
            return true;
        }

        // 检查每个文件条目是否加密
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        for (FileHeader header : fileHeaders) {
            if (header.isEncrypted()) {
                return true;
            }
        }
        
        return false;
    } catch (ZipException e) {
        return false;
    }
    }

    public boolean isZipSplit(String zipFilePath) {
    try {
        return new ZipFile(zipFilePath).isSplitArchive();
    } catch (ZipException e){
        return false;
    }
    }

    public static int[] countFilesAndFoldersInZip(String zipFilePath) throws IOException {
        try {
        ZipFile zipFile = new ZipFile(zipFilePath);
        zipFile.setRunInThread(true);
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            
            // 根据CPU核心数创建线程池
            int threadCount = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            
            // 分割任务列表
            List<List<FileHeader>> partitions = partitionList(fileHeaders, threadCount);
            
            // 提交任务并获取Future
            List<Future<int[]>> futures = new ArrayList<>();
            for (List<FileHeader> partition : partitions) {
                futures.add(executor.submit(new FileCounterTask(partition)));
            }

            // 合并结果
            int totalFiles = 0;
            int totalFolders = 0;
            for (Future<int[]> future : futures) {
                try {
                    int[] partial = future.get();
                    totalFiles += partial[0];
                    totalFolders += partial[1];
                } catch (InterruptedException | ExecutionException e) {
                    executor.shutdownNow();
                    throw new IOException("Task execution failed", e);
                }
            }
            
            executor.shutdown();
            return new int[]{totalFiles, totalFolders};
        } catch (ZipException e) {
            throw new IOException(e);
        }
    }

    // 分割
    private static List<List<FileHeader>> partitionList(List<FileHeader> source, int partitions) {
        List<List<FileHeader>> result = new ArrayList<>();
        int size = source.size();
        int chunkSize = (int) Math.ceil((double) size / partitions);
        
        for (int i = 0; i < partitions; i++) {
            int from = i * chunkSize;
            int to = Math.min(from + chunkSize, size);
            if (from < to) {
                result.add(source.subList(from, to));
            }
        }
        return result;
    }

    // 计数任务
    private static class FileCounterTask implements Callable<int[]> {
        private final List<FileHeader> headers;

        FileCounterTask(List<FileHeader> headers) {
            this.headers = headers;
        }

        @Override
        public int[] call() {
            int files = 0;
            int folders = 0;
            for (FileHeader header : headers) {
                if (header.isDirectory()) {
                    folders++;
                } else {
                    files++;
                }
            }
            return new int[]{files, folders};
        }
    }

    @NonNull
    private String deepcheck(String check) {
        String zipPath = check;

        try {
            // 完整性检查
            boolean isValid = isZipValid(zipPath);
            // 加密状态检查
            boolean isEncrypted = isZipEncrypted(zipPath);
            // 拆分文件检查
            boolean isSplit = isZipSplit(zipPath);
            // 文件/夹计数
            int[] counts = countFilesAndFoldersInZip(zipPath);
            return "ZIP完整性状态: " + (isValid ? "✔ 正常\n" : "✖ 已损坏或加密\n") + "加密保护状态: " + (isEncrypted ? "🔒 已加密\n" : "🔓 未加密\n") + "分卷压缩状态: " + (isSplit ? "📦 拆分文件\n" : "📁 未拆分\n") + "文件数: " + String.valueOf(counts[0]).concat("\n") + "文件夹数: " + String.valueOf(counts[1]);
        } catch (IOException e) {
            return "检查失败，原因: " + e.getMessage();
        }
    }   

    public static boolean verifyIntegrity(String zipFilePath) throws IOException {
        try {
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFilePath);
            Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = entries.nextElement();
                if (!verifyEntryIntegrity(zipFile, entry)) {
                    return false;
                }
            }
            } catch (IOException e) {
                return false;
            }
        return true;
    }

    private static boolean verifyEntryIntegrity(java.util.zip.ZipFile zipFile, java.util.zip.ZipEntry entry) throws IOException {
        byte[] buffer = new byte[8192];
        CRC32 crc32 = new CRC32();
        try {
        InputStream inputStream = zipFile.getInputStream(entry);
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return false;
        }
        long expectedChecksum = entry.getCrc();
        long actualChecksum = crc32.getValue();
        return expectedChecksum == actualChecksum;
    }

    @NonNull
    public static String crccheck(String str) {
        String zipFilePath = str;
        try {
            if (verifyIntegrity(zipFilePath)) {
                return "Zip file is intact.";
            } else {
                return "Zip file is modified or corrupted.";
            }
        } catch (IOException e) {
            return "Error occurred while verifying zip file integrity: " + e.getMessage();
        }
    }

    // 整体测试
    private boolean isZipFile(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            if (file.length() <= RawEndOffset + endOfDirLength) {
                return false;
            }
            long fileLength = file.length();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            // seek到结束标记所在的位置
            randomAccessFile.seek(fileLength - RawEndOffset);
            byte[] end = new byte[endOfDirLength];
            // 读取4个字节
            randomAccessFile.read(end);
            // 关掉文件
            randomAccessFile.close();
            return isEndOfDir(end);
        } else {
            return false;
        }
    }
 
    // 判断是否符合文件夹结束标记
    private boolean isEndOfDir(byte[] src) {
        if (src.length != endOfDirLength) {
            return false;
        }
        for (int i = 0; i < src.length; i++) {
            if (src[i] != endOfDir[i]) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public String commoncheck(String input) {
        String zipFilePath = input;
        File file = new File(zipFilePath);
        if (file.exists()) {
        if (file.canRead()) {
            try {
            boolean isZip = isZipFile(file);
            if (isZip) {
                return "是Zip文件";
            }
            else
            {
                return "非Zip文件或不标准的Zip文件";
            }
            } catch (IOException e) {
                return e.toString();
            }
        } else {
            return "该文件不可读，请检查文件权限";
        }
        } else {
            return "该文件不存在！";
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isZipBomb(String zipFilePath) throws ZipException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        // 检查文件数量是否超过允许的最大值
        if (fileHeaders.size() > MAX_FILE_COUNT) {
            return true;
        }
        // 检查每个文件的大小是否超过允许的最大值
        long totalSize = 0;
        for (FileHeader fileHeader : fileHeaders) {
            totalSize += fileHeader.getUncompressedSize();
            if (totalSize > MAX_TOTAL_FILE_SIZE) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public String bombcheck(String bomb) {
        String zipFilePath = bomb;
        try {
            if (isZipBomb(zipFilePath)) {
                return "Zip Bomb! The size of the file extracted from the ZIP package is too large.";
            } else {
                return "It's Security.";
            }
        } catch (ZipException e) {
            return e.toString();
        }
    }

    @NonNull
    public String adlercalc(String fileadler) {
        String zipFilePath = fileadler;
        try {
        FileInputStream fis = new FileInputStream(zipFilePath);
            Adler32 adler32 = new Adler32();
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                adler32.update(buffer, 0, bytesRead);
            }

            long checksumValue = adler32.getValue();
            String checksumHex = Long.toHexString(checksumValue);

            return "Adler32校验值（Hex）: " + checksumHex;
        } catch (IOException e) {
            return e.toString();
        }
    }

    @NonNull
    public String fakefixer(String fakeEnc) {
        zipcenop ZipCenOp = new zipcenop();
        try {
            ZipCenOp.repair(fakeEnc);
        } catch (IOException e) {
            return e.toString();
        }
        return "修复完毕！请自行查看当前所选ZIP。";
    }

    @NonNull
    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        switch (targetLanguage) {
            case "badcheck":
                return badCheck(text);
            case "rwcheck":
                return various(text);
            case "deepcheck":
                return deepcheck(text);
            case "crccheck":
                return crccheck(text);
            case "commoncheck":
                return commoncheck(text);
            case "bombcheck":
                return bombcheck(text);
            case "adlercalc":
                return adlercalc(text);
            default:
                return fakefixer(text);
        }
    }
}