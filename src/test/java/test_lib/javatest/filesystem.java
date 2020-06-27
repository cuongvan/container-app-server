/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_lib.javatest;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 *
 * @author cuong
 */
public class filesystem {
    public static final String APP_BUILD_DIR = "./app_builds";
    public static void main(String[] args) throws IOException {
        if (Files.notExists(Paths.get(APP_BUILD_DIR))) {
            new File(APP_BUILD_DIR).mkdirs();
            System.out.println("Build directory created");
        }
        
        Path tempDir = Files.createTempDirectory(Paths.get(APP_BUILD_DIR), "");
        System.out.println("ok");
    }
}
