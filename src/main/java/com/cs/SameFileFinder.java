package com.cs;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import java.nio.file.*;
import java.nio.file.attribute.*;

/**
 * Find duplicate files in
 *
 */
public class SameFileFinder
{
    static HashMap<String, String > md5FileName = new HashMap<>();  // This is MD5 -> filename
    static HashSet<String> inodeFileNameSet = new HashSet<>(); // This is seen inodes for hard links.


    public static void main( String[] args )
    {
        iterateFiles("/home/tfox/Downloads");
    }

    static void iterateFiles(String rootPath) {
        try {
            Files.walk(Paths.get(rootPath))
                    .filter(p -> Files.isRegularFile(p))
                    .filter(p -> !inodeFileNameSet.contains(getInodeID(p)))
                    .forEach(p -> {
                        String md5 = getMD5Hash(p);
                        if (md5FileName.containsKey(md5)) {
                            System.out.print("Duplicate Files: " + md5FileName.get(md5) + " and " + p.getFileName() + "\n");
                        } else {
                            md5FileName.put(md5, p.getFileName().toString());
                            inodeFileNameSet.add(getInodeID(p));
                        }
                    });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static String getInodeID(String fileName) throws IOException
    {
        BasicFileAttributes attr = null;
        Path path = Paths.get( fileName);

        attr = Files.readAttributes(path, BasicFileAttributes.class);

        Object fileKey = attr.fileKey();
        String s = fileKey.toString();
        return s.substring(s.indexOf("ino=") + 4, s.indexOf(")"));
    }

    static String getInodeID(Path file) {
        try {
            String s = Files.readAttributes(file, BasicFileAttributes.class).fileKey().toString();
            return s.substring(s.indexOf("ino=") + 4, s.indexOf(")"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getMD5Hash(Path file) {
        try {
            if (Files.isRegularFile(file)) {
                FileInputStream fis = new FileInputStream(file.toFile());
                String md5 = DigestUtils.md5Hex(fis);
                fis.close();
                return md5;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
