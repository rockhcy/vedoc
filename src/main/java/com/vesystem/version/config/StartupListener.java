package com.vesystem.version.config;

import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.module.dto.ReposDto;
import com.vesystem.version.module.entity.Repos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther hcy
 * @create 2020-08-14 20:04
 * @Description
 */
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${gitRootPath}")
    private String gitRootPath;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        systemInit();
    }


    /**
     * 初始化系统资源
     */
    private void systemInit(){
        initRoot();
    }


    private void initRoot(){
        File file = new File(gitRootPath + PathConstant.USER_TEMP_FOLDER_REATIVE_PATH +"1/");
        if ( !file.exists() ){
            file.mkdirs();
        }
    }


}
