package com.vesystem.version;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws GitAPIException, IOException {
        List<String> files = new ArrayList<>();
        files.add("D:/versionTest说明文档.txt");
        files.add("D:/f79868c1-027d-425c-b28f-dcf5e5107e07.jpg");
        //files.add("D:/gitTest");
        String uuidname = UUID.randomUUID().toString();
        List<File> srcFiles = new ArrayList<>();
        srcFiles.add(FileUtil.file(files.get(0)));
        srcFiles.add(FileUtil.file(files.get(1)));
        //srcFiles.add(FileUtil.file(files.get(2)));
        File zipFile = new File("D:\\" + uuidname + ".zip");
        if ( !zipFile.exists() ){
            zipFile.createNewFile();
        }
        ZipOutputStream out = new ZipOutputStream(FileUtil.getOutputStream(zipFile), Charset.forName("UTF-8"));
        for (File f:srcFiles){
            BufferedInputStream in = null;
            try {
                in = FileUtil.getInputStream(f);
                addFile(in, "/"+f.getName(), out);
            } finally {
                IoUtil.close(in);
            }
        }

//
//
//        try (ZipOutputStream out = new ZipOutputStream(FileUtil.getOutputStream(zipFile), Charset.defaultCharset())) {
//            String srcRootDir;
//            for (File srcFile : srcFiles) {
//                if(null == srcFile) {
//                    continue;
//                }
//                // 如果只是压缩一个文件，则需要截取该文件的父目录
//                srcRootDir = srcFile.getCanonicalPath();
//                if (srcFile.isFile() || false) {
//                    //若是文件，则将父目录完整路径都截取掉；若设置包含目录，则将上级目录全部截取掉，保留本目录名
//                    srcRootDir = srcFile.getCanonicalFile().getParentFile().getCanonicalPath();
//                }
//                // 调用递归压缩方法进行目录或文件压缩
//                IoUtil.copy(in, out);
//                ZipUtil.zip(srcFile, srcRootDir, out);
//                out.flush();
//            }
//        } catch (IOException e) {
//            throw new UtilException(e);
//        }
//
//
//        for (String str:files){
//            //ZipUtil.zip(str,"D:\\" + uuidname + ".zip",false);
//            ZipUtil.zip()
//        }
    }

//    private static void zip(File file, String srcRootDir, ZipOutputStream out) throws UtilException {
//        if (file == null) {
//            return;
//        }
//
//        final String subPath = FileUtil.subPath(srcRootDir, file); // 获取文件相对于压缩文件夹根目录的子路径
//        if (file.isDirectory()) {// 如果是目录，则压缩压缩目录中的文件或子目录
//            final File[] files = file.listFiles();
//            if (ArrayUtil.isEmpty(files) && StrUtil.isNotEmpty(subPath)) {
//                // 加入目录，只有空目录时才加入目录，非空时会在创建文件时自动添加父级目录
//                addDir(subPath, out);
//            }
//            // 压缩目录下的子文件或目录
//            for (File childFile : files) {
//                zip(childFile, srcRootDir, out);
//            }
//        } else {// 如果是文件或其它符号，则直接压缩该文件
//            addFile(file, subPath, out);
//        }
//    }

    private void addFile(InputStream in, String path, ZipOutputStream out) throws UtilException {
        if (null == in) {
            return;
        }
        try {
            out.putNextEntry(new ZipEntry(path));
            IoUtil.copy(in, out);
        } catch (IOException e) {
            throw new UtilException(e);
        } finally {
        }
    }

}
