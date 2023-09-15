package org.worker.locks;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockObject {

    private File file;
    private RandomAccessFile randomAccessFile;
    private FileChannel channel;
    private FileLock lock;

    public FileLockObject(File file) {
        this.file = file;
    }

    public void createLock() throws IOException {
        this.randomAccessFile = new RandomAccessFile(file, "rw");
        this.channel = randomAccessFile.getChannel();
        this.lock = channel.lock();
    }

    public void releaseLock() throws IOException {
        this.lock.release();
        this.channel.close();
        this.randomAccessFile.close();
    }



}
