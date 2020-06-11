/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author cuong
 */
public class MyFileUtils {
    public static void copyDirectory(String source, String dest) throws IOException {
        FileUtils.copyDirectory(new File(source), new File(dest));
    }
    
    public static void moveDirectory(String source, String dest) throws IOException {
        FileUtils.moveDirectory(new File(source), new File(dest));
    }
    
    public static void deleteDirectory(String dir) throws IOException {
        FileUtils.deleteDirectory(new File(dir));
    }
    
    
    
    public static void unzipBytesToDir(byte[] in, String dest) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        unzipStreamToDir(is, dest);
    }
    
    public static void unzipStreamToDir(InputStream input, String dest) throws IOException {
        unzipStreamToDir(input, new File(dest));
    }
    
    public static void unzipStreamToDir(InputStream input, File destDir) throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(input)) {
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null) {
                Path entryPath = Paths.get(destDir.toString(), entry.getName());
                if (entry.isDirectory())
                    Files.createDirectories(entryPath);
                else {
                    File newFile = checkZipSlip(destDir, entry);
                    try (FileOutputStream out = new FileOutputStream(newFile)) {
                        IOUtils.copy(zipStream, out);
                    }
                }
                
                zipStream.closeEntry();
                entry = zipStream.getNextEntry();
            }
        }
    }
    
    private static File checkZipSlip(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }
    
    public static void untar(InputStream tarStream, File destDirectory) throws IOException {
        try (
            TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(tarStream))) {
            TarArchiveEntry tarEntry;
            while ((tarEntry = tarIn.getNextTarEntry()) != null) {
                File dest = new File(destDirectory, tarEntry.getName());
                if (tarEntry.isDirectory()) {
                    dest.mkdirs();
                } else {
                    Files.copy(tarIn, dest.toPath());
                }
            }
        }
    }
}
