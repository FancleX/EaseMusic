package edu.northeastern.ease_music_andriod.utils;

import android.media.MediaDataSource;

import java.io.IOException;

public class ByteArrayMediaDataSource extends MediaDataSource {

    private final byte[] data;

    public ByteArrayMediaDataSource(byte[] data) {
        this.data = data;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        int length = (int) Math.min(data.length - position, size);
        if (length <= 0) {
            return -1;
        }
        System.arraycopy(data, (int) position, buffer, offset, length);
        return length;
    }

    @Override
    public long getSize() throws IOException {
        return data.length;
    }

    @Override
    public void close() throws IOException {
    }


}
