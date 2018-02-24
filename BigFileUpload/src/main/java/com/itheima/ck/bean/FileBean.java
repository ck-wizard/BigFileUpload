package com.itheima.ck.bean;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileBean {
    private String name;
    private int chunks;
    private ConcurrentHashMap<Integer, Boolean> map;
    private String md5;

    public FileBean(String name, int chunks, String md5) {
        this.name = name;
        this.chunks = chunks;
        this.md5 = md5;
        map = new ConcurrentHashMap<>();
        for(int i = 0; i < chunks; i++) {
            map.put(i, false);
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

    // 这个值是不准确的...
    public  boolean isLoadComplate() {
        map.values().stream().forEach(System.out::print);
        System.out.println();
        return !map.values().stream().anyMatch((value) -> !value) ;
    }

    public void addIndex(int chunk) {
        map.put(chunk, true);
    }


    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
