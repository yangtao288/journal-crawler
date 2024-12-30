package com.journal.crawler.utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class DownloadPDFUtils {
    public static String waitForDownload(String downloadPath, int timeoutInSeconds) throws Exception {
        // 等待下载完成
        File downloadedFile = waitForDownloadComplete(downloadPath, timeoutInSeconds);
        if (downloadedFile != null) {
            // 重命名文件，移除.crdownload后缀
            String newName = downloadedFile.getAbsolutePath().replace(".crdownload", "");
            if (newName.endsWith("pdf")){
                newName = newName.replace("pdf",".pdf");
            }
            File newFile = new File(newName);
            if (downloadedFile.renameTo(newFile)) {
                return newFile.getAbsolutePath();
            } else {
                return downloadedFile.getAbsolutePath();
            }
        } else {
            return "";
        }
    }

    private static File waitForDownloadComplete(String downloadPath, int timeoutInSeconds) throws InterruptedException {
        File dir = new File(downloadPath);
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < timeoutInSeconds * 1000) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf") || name.endsWith(".pdf.crdownload") || name.endsWith("pdf"));
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!file.getName().endsWith(".crdownload")) {
                        return file; // 文件下载完成
                    }
                    if (file.getName().endsWith(".crdownload") && !isFileLocked(file)) {
                        return file; // .crdownload 文件不再被锁定，认为下载完成
                    }
                }
            }
            TimeUnit.SECONDS.sleep(1);
        }

        return null; // 超时
    }

    private static boolean isFileLocked(File file) {
        try {
            try (java.nio.channels.FileChannel channel =
                         java.nio.channels.FileChannel.open(file.toPath(), java.nio.file.StandardOpenOption.WRITE)) {
                return false; // 文件可以打开进行写入，说明没有被锁定
            }
        } catch (Exception e) {
            return true; // 文件被锁定
        }
    }
}