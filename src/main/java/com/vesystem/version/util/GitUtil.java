package com.vesystem.version.util;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.vesystem.version.module.dto.ReposDto;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

    public static void gitCommit(String path){
        Git git = null;
        try {
            git = Git.init().setDirectory(new File(path)).call();
            git.commit().call();
        } catch (GitAPIException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }finally {
            git.close();
        }
    }

    public static void gitAddFile(String path,String filePath){
        Git git = null;
        try {
            git = Git.init().setDirectory(new File(path)).call();
            git.add().addFilepattern(filePath).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }finally {
            git.close();
        }
    }



}
