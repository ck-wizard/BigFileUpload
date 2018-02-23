package com.itheima.ck.bean;

import java.util.Arrays;
import java.util.stream.Stream;

public class FileBean {
    private String name;
    private int chunks;
    private Boolean[] chunk;
    private String md5;

    public FileBean(String name, int chunks, String md5) {
        this.name = name;
        this.chunks = chunks;
        this.md5 = md5;
        chunk = new Boolean[chunks];
        for(int i = 0; i < chunks; i++) {
            chunk[i] = false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public boolean getChunk(int index) {
        return chunk[index];
    }

    // 默认为true
    public void setChunk(int index) {
        this.setChunk(index, true);
    }

    public void setChunk(int index, boolean value) {
        this.chunk[index] = value;
    }

    public boolean isLoadComplate() {
        return !Arrays.stream(chunk).anyMatch((value) -> !value);
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
