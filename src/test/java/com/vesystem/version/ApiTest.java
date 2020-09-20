package com.vesystem.version;

import cn.hutool.core.io.FileUtil;
import com.vesystem.version.module.dto.DocHistory;
import com.vesystem.version.util.GitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @auther hcy
 * @create 2020-09-01 15:01
 * @Description
 */
public class ApiTest {

    public static void main(String[] args) throws IOException, GitAPIException {
        String dirPath = "D:/versionTest/Repository/1/";
       // GitUtil.gitAddFile(".",dirPath);
//        GitUtil.gitCommit( dirPath,"" );
//        GitUtil.getHistoryLogs(dirPath);
//        GitUtil.rollBackFileRevision(dirPath,"test.txt","fe01ddc03c24dfe2a823ab4cca5f11dd0ec6fba7");
        test(dirPath,"test.txt","fe01ddc03c24dfe2a823ab4cca5f11dd0ec6fba7");
//        System.out.println( GitUtil.getHistoryLogs(dirPath,"test.txt",10) );
    }

    public static void test(String dirPath,String entryPath,String version) throws IOException {
        Git  git = Git.open(new File(dirPath));
        FileOutputStream out = new FileOutputStream(dirPath +entryPath );
        // Repository 包括所有的对象和引用，用来管理源码
        Repository repository = git.getRepository();
        ObjectId objectId = repository.resolve(version);
        //RevWalk 可以遍历提交对象，并按照顺序返回提交对象
        RevWalk revWalk = new RevWalk(repository);
        // RevCommit 代表一个提交对象,这里根据版本号获取到指定版本的提交对象
        RevCommit revCommit = revWalk.parseCommit(objectId);
        //RevTree 代表树对象，根据提交对象获取到树对象。
        RevTree revTree =revCommit.getTree();
        // 获取树径
        //TreeWalk treeWalk = new TreeWalk( repository );//这里是获取到整个仓库的树径
        // 获取到仓库中的指定文件的树径，
        TreeWalk treeWalk = TreeWalk.forPath(repository,entryPath,revTree);
        //treeWalk.setRecursive(false);//为什么要设置循环递归为false？,已经证实不设置也能获取到历史版本文件
        // 获取到单个文件的 ObjectId
        ObjectId objectId2 = treeWalk.getObjectId(0);
        // 将文件写入到流中
        repository.open(objectId2).copyTo(out);
        out.close();
        treeWalk.close();
        repository.close();

    }



}
