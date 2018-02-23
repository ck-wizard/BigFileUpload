package com.itheima.ck.web;


import com.itheima.ck.bean.FileBean;
import com.itheima.ck.bean.FileUploadBean;
import com.itheima.ck.utils.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.util.*;

// 一个http上传大文件实践
public class UploadController extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static String finalDirPath = "C:\\Users\\Administrator\\Downloads\\";

    private Map<String, FileBean> fileMap = Collections.synchronizedMap(new HashMap<String, FileBean>());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean multipartContent = ServletFileUpload.isMultipartContent(req);
        // 是文件上传请求
        if(multipartContent) {
            // 获取请求长度
            int length = req.getIntHeader("Content-Length");
            logger.info("用户请求的长度为:{}", length);

            // 创建工厂（这里用的是工厂模式）
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //获取汽车零件清单与组装说明书（从ServletContext中得到上传来的数据）
            ServletContext servletContext = this.getServletConfig().getServletContext();
            // 临时文件目录
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            //工厂把将要组装的汽车的参数录入工厂自己的系统，因为要根据这些参数开设一条生产线（上传来的文件的各种属性）
            factory.setRepository(repository);
            //此时工厂中已经有了汽车的组装工艺、颜色等属性参数（上传来的文件的大小、文件名等）
            //执行下面的这一行代码意味着根据组装工艺等开设了一条组装生产线
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                List<FileItem> fileItems = upload.parseRequest(req);
                FileUploadBean param = new FileUploadBean(fileItems, logger);
                logger.info("文件信息为:{}",param.toString());

                // 然后存储
                String fileName = param.getName();
                String uploadDirPath = finalDirPath + param.getMd5();
                String tempFileName = fileName + "_" + param.getChunk() + "_tmp";
                Path tmpDir = Paths.get(uploadDirPath);
                if (!Files.exists(tmpDir)) {
                    Files.createDirectory(tmpDir);
                } else {
                    // 文件夹已存在
                    // 1.检查是否有文件,有进入2, 没有进3
                    Path localPath = Paths.get(uploadDirPath, fileName);

                    // 2.检查md5值是否匹配, 应该建立数据库,存储文件信息才是更快 更好的解决办法.
                    // 2.1.若匹配直接返回成功.
                    // 2.2 若不成功,删除源文件再次读取
                    if(Files.exists(localPath)) {
                        String nowMd5 = DigestUtils.md5Hex(Files.newInputStream(localPath, StandardOpenOption.READ));
                        if(StringUtils.equals(param.getMd5(), nowMd5)) {
                            // 比较相等,那么直接返回成功.
                            logger.info("已检测到重复文件{},并且比较md5相等,已直接返回", fileName);
                            return;
                        } else {
                            // 删除
                            logger.info("已经存在的文件的md5不匹配上传上来的文件的md5,删除后重新下载");
                            Files.delete(localPath);
                        }
                    }
                    // 3. 直接写入到具体目录下.
                }


                //写入该分片数据
                // 0.读取文件到文件夹
                // 1.写到本地
                // 1.记录分片数,检查分片数
                // 2.当对应的md5读取数量达到对应的文件后,合并文件
                // 3.删除临时文件
                byte[] fileData = FileUtils.read(param.getFile(), 2048);
                Path path = Paths.get(uploadDirPath, tempFileName);
                Files.write(path, fileData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                FileUtils.authorizationAll(path);

                FileBean fileBean;
                if(fileMap.containsKey(param.getMd5())) {
                    fileBean = fileMap.get(param.getMd5());
                } else {
                    fileBean = new FileBean(param.getName(), param.getChunks(), param.getMd5());
                    fileMap.put(param.getMd5(), fileBean);
                }
                fileBean.setChunk(param.getChunk());

                if(fileBean.isLoadComplate()) {
                    // 合并文件..
                    Path realFile = Paths.get(uploadDirPath, fileBean.getName());
                    realFile = Files.createFile(realFile);
                    // 设置权限
                    FileUtils.authorizationAll(realFile);
                    for(int i = 0 ; i < fileBean.getChunks(); i++) {
                        // 获取每个分片
                        tempFileName = fileName + "_" + i + "_tmp";
                        Path itemPath = Paths.get(uploadDirPath, tempFileName);
                        byte[] bytes = Files.readAllBytes(itemPath);
                        Files.write(realFile, bytes, StandardOpenOption.APPEND);
                        //写完后删除掉临时文件.
                        Files.delete(itemPath);
                    }
                    logger.info("合并文件{}成功", fileName);
                }

            } catch (FileUploadException e) {
                logger.error("解析请求出现异常", e);
            }

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }
}
