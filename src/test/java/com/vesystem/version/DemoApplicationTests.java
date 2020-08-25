package com.vesystem.version;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws GitAPIException {
        String gitRootPath = "D:/versionTest";
        //Git version = Git.init().setDirectory(new File(gitRootPath)).call();
        Git.init().setGitDir(new File("D:\\versionTest\\gitDir")).setDirectory(new File("D:\\versionTest\\1")).call();
       // version.close();
    }

}
