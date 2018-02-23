package com.itheima.ck.bean;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.FileItemHeadersImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileUploadBean {

    private String id;
    private String name;
    private String type;
    private Date lastModifiedDate;
    private int size;
    private int chunks; //分卷总数
    private int chunk; //分卷 0开始
    private InputStream file;
    private String md5;


    public FileUploadBean(List<FileItem> fileItems, Logger logger) {
        fileItems.stream().forEach((fileItem -> {
            String fieldName = fileItem.getFieldName();
            switch (fieldName) {
                case "id" :
                    id = fileItem.getString();
                    break;
                case "name" :
                    try {
                        name = fileItem.getString("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.info("解析文件名称发生异常", e);
                    }
                    break;
                case "type" :
                    type = fileItem.getString();
                    break;
                case "lastModifiedDate" :
                    try {
                        String date = fileItem.getString("UTF-8");
                        lastModifiedDate = new Date(date);
                    } catch (UnsupportedEncodingException e) {
                        logger.info("解析日期格式错误", e);
                    }
                    break;
                case "size" :
                    size = Integer.parseInt(fileItem.getString());
                    break;
                case "chunks" :
                    chunks = Integer.parseInt(fileItem.getString());
                    break;
                case "chunk" :
                    chunk = Integer.parseInt(fileItem.getString());
                    break;
                case "file" :
                    try {
                        file = fileItem.getInputStream();
                    } catch (IOException e) {
                        logger.error("获取输入流时出现错误", e);
                    }
                    break;
                case "md5" :
                    md5 = fileItem.getString();
                    break;
            }
        }));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "FileUploadBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", size=" + size +
                ", chunks=" + chunks +
                ", chunk=" + chunk +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
