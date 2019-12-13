/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author cuong
 */
public class MFileUtils {
    public static void unzipStreamToDir(InputStream input, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(input)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                Path entryPath = Paths.get(destDir.toString(), entry.getName());
                if (entry.isDirectory())
                    Files.createDirectories(entryPath);
                else {
                    File newFile = checkZipSlip(destDir, entry);
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        IOUtils.copy(zis, fos);
                    }
                }
                
                zis.closeEntry();
                entry = zis.getNextEntry();
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
}
