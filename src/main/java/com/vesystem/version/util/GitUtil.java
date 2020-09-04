package com.vesystem.version.util;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.vesystem.version.module.dto.DocHistory;
import com.vesystem.version.module.dto.ReposDto;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @auther hcy
 * @create 2020-08-14 19:34
 * @Description
 */
public class GitUtil {
    private static Logger log = LoggerFactory.getLogger(GitUtil.class);
    public static boolean initGitRepository(ReposDto reposDto){
        File file = new File(reposDto.getRepoPath());
        if( !file.exists()){
            file.mkdirs();
        }
        //初始化一个Git仓库
        Git git = null;
        try {
            git = Git.init().setDirectory(file).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }finally {
            git.close();
        }
        return true;
    }

    /**
     *  添加文件
     * @param filepattern
     * @param dirPath
     */
    public static void gitAddFile(String filepattern,String dirPath){
        Git git = null;
        try {
            Git.open(new File(dirPath)).add().addFilepattern(filepattern).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("git 添加文件时发生错误",e);
        } finally {
            if ( git !=null ){
                git.close();
            }
        }
    }

    /**
     * 提交文件
     * @param dirPath
     * @param commitMsg
     */
    public static void gitCommit(String dirPath,String commitMsg){
        if (StringUtils.isEmpty( commitMsg )){
            commitMsg = "";
        }
        Git git = null;
        try {
            Git.open(new File(dirPath)).commit().setMessage(commitMsg).call();
        } catch (GitAPIException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        } finally {
            if ( git !=null ){
                git.close();
            }
        }
    }

    /**
     * 获取指定文件的历史版本记录
     * @param dirPath  仓库物理路径
     * @param entryPath 仓库中的具体文件名称，注意：当前版本只支持下载单个文件的历史版本。后期可以考虑做文件夹的整体版本回退
     *  @param maxLogNum 要获取的日志条数，默认是按时间到排序的
     */
    public static List<DocHistory> getHistoryLogs(String dirPath,String entryPath,int maxLogNum) throws IOException, GitAPIException{
        List<DocHistory> list = new ArrayList<>();
        Git git = Git.open(new File( dirPath ));
        Iterable<RevCommit> iterable = git.log().addPath( entryPath ).setMaxCount( maxLogNum ).call();
        Iterator<RevCommit> iter=iterable.iterator();
        while ( iter.hasNext() ){
            RevCommit commit = iter.next();
            // 获取到提交人
            String commitUser = commit.getCommitterIdent().getName();
            // 提交时间
            long commitTime = commit.getCommitTime() * 1000L;
            // 提交信息
            String fullMessage = commit.getFullMessage();
            // 提交id
            String commitId = commit.getName();
            DocHistory docHistory = new DocHistory();
            docHistory.setTime( commitTime );
            docHistory.setCommitMsg( fullMessage );
            docHistory.setObjectId( commitId );
            docHistory.setEntryName( entryPath );
            docHistory.setRepoPath( dirPath );
            docHistory.setSubmitter( commitUser );
            //docHistory.setRepoId();
            list.add( docHistory );
        }
        return list;
    }

    /**
     * 获取某个文件的历史版本，用于下载历史版本文件，或者将历史版本文件写到其他路径中。
     * @param version 版本号
     * @param dirPath 仓库物理路径
     * @param entryPath 仓库中的具体文件名称，注意：当前版本只支持下载单个文件的历史版本。后期可以考虑做文件夹的整体版本回退
     * @param copyFilePath 将历史版本写入到哪个文件夹下
     * @throws IOException 统一返回：获取历史版本文件时发生错误
     */
    public static void getHistoryVersionFile(String version, String dirPath,String entryPath,String copyFilePath) throws IOException{
        Git  git = Git.open(new File(dirPath));
        FileOutputStream out = new FileOutputStream(copyFilePath);
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

    /**
     * 单文件版本回退
     * 本质就是将单个文件的历史版本写入当前工作区，然后再提交。这样这一次的版本回退也会称为一个版本节点。
     * 目前还不清楚这个处理方式和 "git reset HEAD^ 文件" 这种方式有什么差别.
     * 不建议使用改方法，请在serviceImpl层自己判断锁标识后调用 getHistoryVersionFile()
     * @param dirPath
     * @param entryPath
     * @param version
     * @throws IOException
     */
    @Deprecated
    public static void fileVersionRevert(String dirPath,String entryPath,String version) throws IOException {
        //检查文件是否被加锁
        //对文件加锁
//        getHistoryVersionFile(version,dirPath,entryPath,dirPath+entryPath  );
        //提交版本，备注信息为:XXX文件 回退至版本version，
        //解除文件锁
    }

}
