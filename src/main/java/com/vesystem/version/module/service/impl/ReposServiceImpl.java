package com.vesystem.version.module.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.exceptionHandler.ErrorCode;
import com.vesystem.version.exceptionHandler.ParameterInvalid;
import com.vesystem.version.module.dto.*;
import com.vesystem.version.module.entity.*;
import com.vesystem.version.module.mapper.*;
import com.vesystem.version.module.service.IReposService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vesystem.version.util.CurrentUserUtils;
import com.vesystem.version.util.GitUtil;
import com.vesystem.version.util.JwtToken;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2020-08-24
 */
@Service
public class ReposServiceImpl extends ServiceImpl<ReposMapper, Repos> implements IReposService {

    private static final Logger log = LoggerFactory.getLogger(ReposServiceImpl.class);
    @Autowired
    private CurrentUserUtils currentUserUtils;
    @Autowired
    private ReposMapper reposMapper;
    @Autowired
    private MappingRepoUserMapper mappingRepoUserMapper;

    @Autowired
    private ReposShareMapper reposShareMapper;
    @Autowired
    private DocLockMapper docLockMapper;
    @Autowired
    private FileFingerprintMapper fileFingerprintMapper;

    @Value("${gitRootPath}")
    private String gitRootPath;
    @Value("${deleteFolderWhenDeleteRepo}")
    private Boolean deleteFolderWhenDeleteRepo;
    @Value("${openFileSecondPass}")
    private Boolean openFileSecondPass;
    @Value("${maxHistoryRollbackSize}")
    private Integer maxHistoryRollbackSize;





    public void downloadRevision(String dirPath, String entryPath, String version, HttpServletResponse response){
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        File file = new File( dirPath + entryPath );
        if ( !file.exists() ){
            throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
        }
        try {

            // 下面两句设置后axois才能顺利从响应头中获取到文件名，浏览器默认只能获取到响应头中的Content-Type等2个属性，其他的都要单独添加
            response.addHeader("filename",URLEncoder.encode(file.getName(),"UTF-8"));
            response.addHeader("Access-Control-Expose-Headers","filename");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.JAVA_SERVICE_NOT_SUPPORT_UTF_8);
        }
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            GitUtil.getHistoryVersionFile(version,dirPath,entryPath,out);
        } catch (IOException e) {
            e.printStackTrace();
            if ( out !=null ){
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw new ParameterInvalid(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public void rollBackRevisionByVersionId(String dirPath,String entryPath,String version){
        try {
            GitUtil.rollBackFileRevision(dirPath,entryPath,version);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
        } catch (GitAPIException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public void rollBackRevisionToPreviousVersion(String dirPath,String entryPath){
        try {
            List<DocHistory> list = GitUtil.getHistoryLogs(dirPath,entryPath,2);
            if ( list.size() <2 ){
                throw new ParameterInvalid(ErrorCode.NOT_FOUND_FALLBACK_VERSION);
            }
            rollBackRevisionByVersionId(dirPath,entryPath,list.get(1).getObjectId());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

    }



    public JSONArray getFileList(String path){
        //该方法中的path就直接使用文件的物理路径就好，如果担心泄露物理地址导致安全性问题，可以对该字段加密。反正前端也不用解析这个字段
        File file = new File(path);
        if ( !file.exists() ){
            throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
        }
        if ( !path.startsWith( gitRootPath ) ){
            //获取文件时检查，如果发现不是以定义的根路径开始的，就终端请求
            throw new ParameterInvalid(ErrorCode.INVALID_PATH);
        }
        File[] files = file.listFiles();
        JSONArray jsonArray = new JSONArray();
        for (File f :files ){
            if ( !f.isHidden() ){
                DocDto doc = new DocDto();
                doc.setName( f.getName() );
                doc.setModifyTime( f.lastModified() );
                doc.setSize( f.length() );
                if ( f.isDirectory() ){
                    doc.setType( 1 );
                    doc.setPath( PathConstant.tranformPathWhenOSIsWindows(f.getPath())+"/" );
                }else {
                    doc.setType( 2 );
                    doc.setPath( PathConstant.tranformPathWhenOSIsWindows(f.getPath()) );
                }
                jsonArray.add( doc );
            }
        }
        return jsonArray;
    }

    public JSONObject getHomeReposList(HttpServletRequest request){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        Map<String,List<ReposDto>> map = new HashMap<>();
        //普通仓库
        List<ReposDto> r1 = new ArrayList<>();
        //版本仓库
        List<ReposDto> r2 = new ArrayList<>();
        //协作仓库
        List<ReposDto> r3 = new ArrayList<>();
        // 1.先查出自己创建的仓库
        List<ReposDto> list = reposMapper.selectReposByCreateId(userDto.getUserId());
        list.forEach( r ->{
            r.setIsSelfCreate(true);
            if ( Objects.equals( r.getRepoType(),1 ) ){
                r1.add( r );
            }else if ( Objects.equals( r.getRepoType(),2 ) ){
                r2.add( r );
            }else if ( Objects.equals( r.getRepoType(),3 ) ){
                r3.add( r );
            }
        });
        //然后再查出协同仓库
        List<ReposDto> mappingList = reposMapper.selectMapppingReposListByUserId( userDto.getUserId() );
        mappingList.forEach( r ->{
            r.setIsSelfCreate(false);
            if ( Objects.equals( r.getRepoType(),2 ) ){
                r2.add( r );
            }else if ( Objects.equals( r.getRepoType(),3 ) ){
                r3.add( r );
            }
        } );
        JSONObject rjson = new JSONObject();
        rjson.put("1",r1);
        rjson.put("2",r2);
        rjson.put("3",r3);
        return rjson;
    }

    public void addRepo(HttpServletRequest request,ReposDto reposDto){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        reposDto.setCreater(userDto.getUsername());
        reposDto.setCreateTime(new Date());
        reposDto.setCreaterId(userDto.getUserId());
        reposMapper.insert(reposDto);
        reposDto.setRepoPath( gitRootPath + PathConstant.GIT_REPOS_REATIVE_PATH + reposDto.getRepoId() + "/");
        if ( !GitUtil.initGitRepository(reposDto) ){
            throw new ParameterInvalid(ErrorCode.INIT_REPOS_ERROR);
        }
        //注意这里使用先插入在更新的逻辑，放弃查询最后一条记录将id+1的处理方式,避免并发id被其他线程使用
        reposMapper.updateRepoPath(reposDto.getRepoPath(),reposDto.getRepoId());
    }

    public List<Repos> getSelfRepoList(HttpServletRequest request){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<Repos> qw = new QueryWrapper<>();
        qw.eq("creater_id",userDto.getUserId());
        return reposMapper.selectList( qw );
    }

    public void deleteSelfRepo(HttpServletRequest request,Integer repoId){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        Repos dbRepo = reposMapper.selectById(repoId);
        if ( dbRepo == null ){
            throw new ParameterInvalid(ErrorCode.REPO_NOT_EXIST);
        }
        if ( !Objects.equals( userDto.getUserId(),dbRepo.getCreaterId() )  && !Objects.equals( userDto.getUserId(),"1" )){
            //只允许删除自己创建的仓库，root拥有特权
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        QueryWrapper<Repos> qw = new QueryWrapper<>();
        qw.eq("repo_id",dbRepo.getRepoId());
        reposMapper.delete(qw);
        if (deleteFolderWhenDeleteRepo){
            if ( !FileUtil.del(dbRepo.getRepoPath()) ){
                log.error("同步删除仓库目录时，因为未知原因未能删除该目录，请手工删除。目录地址："+ dbRepo.getRepoName() );
            }
        }
    }

    public void addMappingRepo(HttpServletRequest request,Integer shareId,String password){
        ReposShare rs= reposShareMapper.selectById(shareId);
        if (rs ==null){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        if ( !StringUtils.isEmpty( rs.getRepoPwd() ) && !Objects.equals( rs.getRepoPwd(),password ) ){
            throw new ParameterInvalid(ErrorCode.REPO_SHARE_PASSWORD_ERROR);
        }
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        Repos r = reposMapper.selectById( rs.getRepoId() );
        //分享他人仓库，默认权限为：1 1 0 1
        mappingRepoUserMapper.insert(new MappingRepoUser(rs.getRepoId(),userDto.getUserId(),true,true,false,true,r.getRepoName()));
    }

    public void deleteMappingRepo(HttpServletRequest request,Integer repoId){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<MappingRepoUser> qw = new QueryWrapper<>();
        qw.eq("repo_id",repoId).eq("user_id",userDto.getUserId() );
        mappingRepoUserMapper.delete(qw);
    }

    public List<MappingRepoUser> getMappingRepoList(HttpServletRequest request){
        UserDto userDto = currentUserUtils.getCurrentUser(request);
        QueryWrapper<MappingRepoUser> qw = new QueryWrapper<>();
        qw.eq("user_id",userDto.getUserId());
        return mappingRepoUserMapper.selectList( qw );
    }

    public void smallFileUpload(HttpServletRequest request,String savePath){
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartFile file =multiRequest.getFile("file");
        if ( file.getSize() > PathConstant.MULTIPART_FILE_THRESHOLD ){
            throw new ParameterInvalid(ErrorCode.PLEASE_USE_MULTIPART_UPLOAD);
        }
        String filePath = savePath + file.getOriginalFilename();
        log.info( "file.getOriginalFilename()---->>"+file.getOriginalFilename() );
        filePath = PathConstant.tranformPathWhenOSIsWindows(filePath);
        String username = JwtToken.getUsernameByRequest(request);
        checkFileLock(PathConstant.tranformPathWhenOSIsWindows(filePath));
        addFileLock(username,DocLock.TYPE_FILE,filePath);
        File file1 = new File(filePath);
        try {
            file.transferTo(file1);
        } catch (IOException e) {
            log.info( "上传小文件时发生错误",e );
        }
        delFileLock(username,filePath);
    }

    public JSONObject multipartUpload(HttpServletRequest request,MultipartParam param ){
        //最后验证这个方法，这个玩意不太好用postman测试
        JSONObject j =null;
        switch (param.getPhase()){
            case "start":
                j = MultipartUploadStart(request,param.getPath(), param.getName(),param.getSize());
                break;
            case "upload":
                j =MultipartUpload(request,param.getPath(),param.getSession_id(),param.getStart_offset());
                break;
            case "finish":
                j = MultipartUploadFinish(request,param.getPath(),param.getSession_id(),param.getMd5());
                break;
            default:
                throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        return j;
    }
    public JSONObject MultipartUploadStart(HttpServletRequest request,String savePath,String fileName,Long fileSize){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        checkFileLock(filePath);
        addFileLock( JwtToken.getUsernameByRequest(request),DocLock.TYPE_FILE,filePath );
        RandomAccessFile raf =null;
        try {
            File file = new File(filePath);
            raf = new RandomAccessFile(file,"rw");
            raf.setLength(fileSize);
        }catch (IOException e){
            log.error("分片上传文件时发生错误：",e);
        }finally {
            try {
                raf.close();
            } catch (IOException e) {
                log.error("关闭分片中的 RandomAccessFile流时发生错误：",e);
            }
        }
        JSONObject j1 = new JSONObject();
        j1.put("end_offset", PathConstant.MULTIPART_FILE_THRESHOLD);
        j1.put("session_id", fileName);
        return j1;
    }
    public JSONObject MultipartUpload(HttpServletRequest request,String savePath,String fileName,Long startOffset){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        JSONObject j = new JSONObject();
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartFile file =multiRequest.getFile("file");
        File multipartFile = new File(filePath);
        RandomAccessFile raf =null;
        try {
            raf = new RandomAccessFile(multipartFile,"rw");
            raf.seek(startOffset);//移动指针
            raf.write(file.getBytes());
            j.put("status","success");
        }catch (IOException e){
            log.error("分片上传文件时发生错误：",e);
            j.put("status","err");
        }finally {
            try {
                raf.close();
            } catch (IOException e) {
                log.error("分片上传文件时发生错误：",e);
                j.put("status","err");
            }
        }
        return j;
    }
    public JSONObject MultipartUploadFinish(HttpServletRequest request,String savePath,String fileName,String fileMd5){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        JSONObject j = new JSONObject();
        MD5 md5 = new MD5();
        File file = new File(filePath);
        String m = md5.digestHex(file);
        if ( Objects.equals( fileMd5 ,m) ){
            j.put("status","success");
            if ( openFileSecondPass ){
                FileFingerprint fileFingerprint = new FileFingerprint();
                fileFingerprint.setHash(m);
                fileFingerprint.setFilePath( filePath );
                fileFingerprint.setIsExist(true);
                fileFingerprintMapper.insert( fileFingerprint );
            }
        }else {
            file.delete();
            j.put("status","err");
        }
        delFileLock(JwtToken.getUsernameByRequest(request),filePath);
        return j;
    }

    public void deleteFile(HttpServletRequest request,String filePath){
        filePath = PathConstant.tranformPathWhenOSIsWindows(filePath);
        if ( !FileUtil.exist(filePath) ){
            throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
        }
        String username = JwtToken.getUsernameByRequest(request);
        checkFileLock(filePath);
        // 可以删除文件，也能删除文件夹,这里统一加文件夹锁
        addFileLock(username,DocLock.TYPE_FOLDER,filePath);
        FileUtil.del(filePath);
        delFileLock(username,filePath);
    }

    public List<DocHistory> getDocHistoryList(String repoPath,String path){
        try {
            return GitUtil.getHistoryLogs(repoPath,path.replaceAll(repoPath,""),maxHistoryRollbackSize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
        } catch (GitAPIException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * 检查文件锁，如果文件已经被锁定就结束请求
     * @param path
     */
    public void checkFileLock(String path){
        QueryWrapper<DocLock> qw = new QueryWrapper<>();
        qw.eq("path",path).orderByDesc("lock_time");
        List<DocLock> locks = docLockMapper.selectList(qw);
        if ( locks.size() > 0 ){
            if ( locks.get(0).getLockTime() > System.currentTimeMillis() ){
                throw new ParameterInvalid( ErrorCode.FILE_LOCKING );
            }
        }
    }

    /**
     * 追加文件锁
     * @param username
     * @param path
     */
    public void addFileLock(String username,Integer type,String path){
        // 添加文件锁前需要确认该文件是否已经存在锁标识
        checkFileLock(path);
        DocLock docLock = new DocLock();
        docLock.setType(type);
        docLock.setState(type);
        docLock.setPath( path );
        docLock.setLocker( username );
        docLock.setLockTime( System.currentTimeMillis() + JwtToken.EXPIRATION );
        docLockMapper.insert( docLock );
    }

    /**
     * 删除锁标志
     * @param username
     * @param path
     */
    public void delFileLock(String username,String path){
        QueryWrapper<DocLock> qw = new QueryWrapper<>();
        qw.eq("path",path).eq("locker",username);
        docLockMapper.delete(qw);
    }


}
