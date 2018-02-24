# BigFileUpload

![wen icon](https://avatars3.githubusercontent.com/u/17871201?s=400&u=4619fbd6026e65618aa93cf1803d023adc08b8da&v=4)

## 目录
* [背景介绍](#背景介绍)
* [项目介绍](#项目介绍)
* [使用说明](#使用说明)
  * [获取代码](#获取代码)
  * [需要知识点](#需要知识点)
  * [启动项目](#启动项目)
  * [项目示范](#项目示范) 
* [核心讲解](#功能分析)
  * [重要原理](#重要原理)
* [功能分析](#功能分析)
  * [分块上传](#分块上传)
  * [秒传功能](#秒传功能)
  * [断点续传](#断点续传)
* [总结](#总结)

<a name="背景介绍"></a>
## 背景介绍
这个项目是在朋友的一次面试中,面试人提出了一个问题.
**我有一个100M的文件,然后我的宽带只有10M,我应该如何处理用户上传的文件?**
根据这个问题,我小试牛刀,写了这个项目.

期间查阅了资料,借鉴了[Fourwen](https://github.com/Fourwenwen/Breakpoint-http)的项目的前端框架和md写法.

再次感谢.

<a name="项目介绍"></a>
## 项目介绍
项目采用如下:

- 上层: Java, JDK8, Tomcat8,
- 服务端: Jsp, 原生
- 前端: webuploader, bootstrap, jquery

来进行开发,

针对文件的上传,一般可以考虑的功能点有

 **断点续传** 在断网或者在暂停的情况下，能够在上传断点中继续上传。 
 
 **分块上传** 也是断点续传的基础之一，把大文件通过前端分块，然后后台在组在一起。 
 
 **文件秒传** 服务中已经有人上传过文件，其他人再上传这个文件直接记录并放回成功。 
 
 **其他功能** 下面这些功能归类到其他，是因为它们基本都是通过[WebUploader](http://fex.baidu.com/webuploader)来实现的，很简单。 
   
    - 多线程上传 多个线程上传不同的块文件。 
    - 文件进度显示 显示文件的上传完成情况。 
    
<a name="获取代码"></a>
## 获取代码
- GitHub:https://github.com/ck-wizard/BigFileUpload

不会经常更新,下一步会做一个集合公司内部网址的项目.


<a name="需要知识点"></a>
## 需要知识点
- 项目使用nio来进行文件的读取和创建
- 使用原生web来开发,不使用任何框架
- 使用Apache提供的fileupload来实现上传数据的获取
- 使用Apache提供的codec来实现md5加密
- 并发的理解

<a name="启动项目"></a>
## 启动项目
...

<a name="项目示范"></a>
## 项目示范
...

<a name="功能分析"></a>
## 功能分析
分块上传可以说是我们整个项目的基础，像断点续传、暂停这些都是需要用到分块。 
分块这块相对来说比较简单。前端是采用了webuploader，分块等基础功能已经封装起来，使用方便。 
借助webUpload提供给我们的文件API,前端就显得异常简单。

```angular2html
   var uploader = WebUploader.create({

        // swf文件路径
        swf: '${ctx}/webuploader-0.1.5/Uploader.swf',

        // 文件接收服务端。
        server: '${ctx}/upload.do',
        //文件上传请求的参数表，每次发送都会发送此对象中的参数
        formData: {
            md5: ''
        },

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        chunked: true, // 分块
        chunkSize: 1 * 1024 * 1024, // 字节 1M分块
        threads: 3, //开启线程
        auto: false,

        // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
        disableGlobalDnd: true,
        fileNumLimit: 1024,
        fileSizeLimit: 200 * 1024 * 1024,    // 200 M
        fileSingleSizeLimit: 100 * 1024 * 1024    // 100 M
    });
```

上传的文件会被发送到upload.do这个Controller,在里面的逻辑如下:

1. 判断是文件上传请求,如果是继续,否则退出
2. 使用fileupload jar包解析request请求上传的基础信息
3. 使用FileUploadBean包装上传的基础信息.
4. 拼装父目录,校验是否存在
```
    4.1 不存在就创建
    4.2 存在就进入检验
    4.2.1 检查md5值是否匹配, 应该建立数据库,存储文件信息才是更快 更好的解决办法.
    4.2.2 若匹配直接返回成功.
    4.2.3 若不成功,删除源文件再次读取
```
5. 写入该分片数据到指定目录
```angular2html
    写入规则如下:
    // 0.读取上传文件到数组
    // 1.写到本地
    // 1.记录分片数,检查分片数
    // 2.当对应的md5读取数量达到对应的文件后,合并文件
    // 3.删除临时文件
```
6. 完成

## 功能分析
<a name="分块上传"></a>
### 分块上传
分块上传可以说是我们整个项目的基础，像断点续传、暂停这些都是需要用到分块。 
分块这块相对来说比较简单。前端是采用了webuploader，分块等基础功能已经封装起来，使用方便。 
借助webUpload提供给我们的文件API,前端就显得异常简单。

```angular2html
       var uploader = WebUploader.create({
    
            // swf文件路径
            swf: '${ctx}/webuploader-0.1.5/Uploader.swf',
    
            // 文件接收服务端。
            server: '${ctx}/upload.do',
            //文件上传请求的参数表，每次发送都会发送此对象中的参数
            formData: {
                md5: ''
            },
    
            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: '#picker',
    
            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false,
    
            chunked: true, // 分块
            chunkSize: 1 * 1024 * 1024, // 字节 1M分块
            threads: 3, //开启线程
            auto: false,
    
            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd: true,
            fileNumLimit: 1024,
            fileSizeLimit: 200 * 1024 * 1024,    // 200 M
            fileSingleSizeLimit: 100 * 1024 * 1024    // 100 M
        });
```

服务器先创建一个md5文件夹,然后按照上传的文件名进行一套规范命名,写入到一个临时文件中.
然后记录这个临时文件.

```angular2html
    // 规范命名
    String fileName = param.getName();
    String uploadDirPath = finalDirPath + param.getMd5();
    String tempFileName = fileName + "_" + param.getChunk() + "_tmp";
    Path tmpDir = Paths.get(uploadDirPath);
    // 写入临时文件
    Path path = Paths.get(uploadDirPath, tempFileName);
    byte[] fileData = FileUtils.read(param.getFile(), 2048);
    Files.write(path, fileData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    FileUtils.authorizationAll(path);
    // 记录
    FileBean fileBean;
    if(fileMap.containsKey(param.getMd5())) {
        fileBean = fileMap.get(param.getMd5());
    } else {
        fileBean = new FileBean(param.getName(), param.getChunks(), param.getMd5());
        fileMap.put(param.getMd5(), fileBean);
    }
    fileBean.setChunk(param.getChunk());
```

然后当文件分片都上传完成后,在把分片合并为一个文件,并且删除所有临时文件.

```angular2html
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
```

<a name="秒传功能"></a>
## 秒传功能

上传文件是若发现父目录已经创建,并且目录下有上传的文件名,那么进行md5比较,若相同,直接返回,若不相同,删除目录文件,重新上传.

```angular2html
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
```

<a name="断点续传"></a>
## 断点续传
断点续传，就是在文件上传的过程中发生了中断，人为因素（暂停）或者不可抗力（断网或者网络差）导致了文件上传到一半失败了。然后在环境恢复的时候，重新上传该文件，而不至于是从新开始上传的。 

文件上传时,获取分片大小,同服务器目录存储的分片大小进行比较,若一直,直接返回成功.
```angular2html
    //写入该分片数据
    Path path = Paths.get(uploadDirPath, tempFileName);
    //文件上传时,获取是否有分片,如果有直接返回.
    if(!Files.exists(path)) {
        // 不存在
        byte[] fileData = FileUtils.read(param.getFile(), 2048);
        try {
            Files.write(path, fileData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException e) {
            // 删除上传的文件
            Files.delete(path);
            throw e;
        }
        FileUtils.authorizationAll(path);
    } else {
        return;
    }
```

<a name="总结"></a>
## 总结

选择使用原生是为了锻炼自己不要忘记基础,前前后后写了3天,复习了不少文件相关的操作,并且对lambda表达式和流
有了进一步了解,还是很满足的.

在并发的情况下进行文件上传,在使用一个实例的成员变量进行存储的时候,在方法上面使用synchronized或代码段加synchronized
或Lock或使用AtomInteger去进行并发操作,都没能达到正确统计的目的.最后使用ConcurrentHashMap才完成了正确的计数.

由此看来,多线程环境下,我还是个小菜鸟啊. 努力加油了.

