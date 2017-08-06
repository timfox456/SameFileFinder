# SameFileFinder

Written By Tim Fox

## Introduction

This is in response to a programming challenge.

A Linux directory structure contains 100G worth of files. The
depth and number of sub-directories and files is not known.
Soft-links and hard-links can also be expected.  Write, in
the language of your choice, a program that traverses the
whole structure as fast as possible and reports duplicate
files. Duplicates are files with same content.
Be prepared to discuss the strategy that you've taken and its trade-offs.

## Discussion

This program is written in Java.  Java is not going to be the fastest
solution.  We really should do this in native code (C) to make this
optimal.

I decided to do the java nio file traversal.  I weighed this against
using the apache Commons IO file traversal, but reportedly this is slow.
The java nio file traversal isn't exactly fast.

We detect the files by computing the MD5 hash of the contents. MD5 is 
not cryptographically secure but for this purpose it's fine and may be faster
than more secure alternatives.

This is a single-threaded application that could likely be faster by 
multithreading.

### Links

A discussion of soft and hard links.

Hard links are multiple links to the same file.  These will have the same
inode number as the other links to the file.  So, to detect this, we have
to get the inode ID.  Unfortunately there is not a language independent
way in Java to do this.  If we see the same inode ID, then this file 
is NOT a duplicate file, because it's the same inode as the previous.

Symbolic links we want to ignore completely.   We also want to make sure
that our file traversing code is not confused by symbolic links.

### Running

Build using maven.

```bash
  mvn clean package
```




