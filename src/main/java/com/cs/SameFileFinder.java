package com.cs;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;

import java.nio.file.*;
import java.nio.file.attribute.*;

/**
 * Detects if a file is duplicated.  Takes a command line parameter for the root path.
 */
public class SameFileFinder
{
    /**
     * A HashMap we use for indexing the MD5 to the path
     */
    static HashMap<String, String > md5FileName = new HashMap<>();  // This is MD5 -> filename
    /**
     * A HashSet we use for previously used inode numbers,
     */
    static HashSet<String> inodeFileNameSet = new HashSet<>(); // This is seen inodes for hard links.

    /**
     * Main class to be invoked from the command line. Pass in root directory.
     * @param args
     */
    public static void main( String[] args )
    {
        //TODO: use command line parser library if
        if (args.length == 1 && args[0].length() > 0)
            iterateFiles(args[0]);
        else
            System.err.println("Usage: SameFileFinder <rootdir>");
    }

    /**
     * Performs the file walking.  Will write out duplicate files to stdout.
     * @param rootPath
     */
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

    /**
     * Gets the inode ID. Note this only works on POSIX systems.
     * @param file
     * @return The inode ID as a string
     */
    static String getInodeID(Path file) {
        try {
            String s = Files.readAttributes(file, BasicFileAttributes.class).fileKey().toString();
            return s.substring(s.indexOf("ino=") + 4, s.indexOf(")"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the MD5 hash by using the apache commons library
     * @param file
     * @return MD5 as string.
     */
    static String getMD5Hash(Path file) {
        //TODO: Try not to abuse the garbage collector like this.
        try (FileInputStream fis = new FileInputStream(file.toFile()) ){
            return DigestUtils.md5Hex(fis);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
