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

    }



}
