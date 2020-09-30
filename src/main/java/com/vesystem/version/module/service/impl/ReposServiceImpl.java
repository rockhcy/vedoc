package com.vesystem.version.module.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vesystem.version.constants.PathConstant;
import com.vesystem.version.enums.ReposTypeEnums;
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
import com.vesystem.version.util.OtherUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
    /**
     * 分片缓存set，上传开始时检查文件是否存在，如果存在就写入set中，在上传完成后git提交信息为"覆盖上传"，
     * 如果文件是不存在的，提交信息就是"上传"
     */
    private Set<String> shardToUploadCheckFileIsExist = new CopyOnWriteArraySet<>();




    public Boolean enterRepo(Integer repoId){
        Repos repos = reposMapper.selectById(repoId);
        return StringUtils.isEmpty(repos.getRemotePwd());
    }

    public Boolean checkRepoPassword(String password,Integer repoId){
        Repos repos = reposMapper.selectById(repoId);
        return Objects.equals( repos.getRemotePwd(),password );
    }

    public Boolean createFolder(String path,String folderName){
        File file = new File(path + folderName);
        return file.mkdirs();
    }

    public void batchDownload(HttpServletRequest request,HttpServletResponse response,List<String> files){
        if ( files.size()<1 ){
            throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.addHeader("Access-Control-Expose-Headers","filename");
        ServletOutputStream out = null;
        try {
            Integer userId = JwtToken.getUserIdByRequest(request);
            out = response.getOutputStream();
            File tempFile ;
            if ( files.size() ==1 ){
                File file = new File(files.get(0));
                if ( !file.exists() ){
                    throw new ParameterInvalid(ErrorCode.PATH_NOT_EXIST);
                }
                //如果是文件夹就打包文件
                if ( file.isDirectory() ){
                    String uuid =  UUID.randomUUID().toString();
                    String tempFileName = gitRootPath + PathConstant.USER_TEMP_FOLDER_REATIVE_PATH + userId + "/"+uuid + ".zip";
                    tempFile = ZipUtil.zip( files.get(0),tempFileName );
                    response.addHeader("filename",URLEncoder.encode(tempFile.getName(),"UTF-8"));
                    ServletUtil.write(response,tempFile);
                    FileUtil.del(tempFileName);
                }else {
                    // 直接写出文件
                    tempFile = file;
                    response.addHeader("filename",URLEncoder.encode(tempFile.getName(),"UTF-8"));
                    ServletUtil.write(response,tempFile);
                }
            }else {
                //多文件下载就打包文件
                String uuid =  UUID.randomUUID().toString();
                String tempFolder = gitRootPath + PathConstant.USER_TEMP_FOLDER_REATIVE_PATH + userId + "/"+uuid+"/";
                File f =new File(tempFolder);
                if ( !f.exists() ){
                    f.mkdirs();
                }
                for (String s:files){
                    FileUtil.copy(s,tempFolder,true);
                }
                String tempFileName = gitRootPath + PathConstant.USER_TEMP_FOLDER_REATIVE_PATH + userId + "/"+uuid + ".zip";
                tempFile = ZipUtil.zip(tempFolder,tempFileName);
                response.addHeader("filename",URLEncoder.encode(tempFile.getName(),"UTF-8"));
                ServletUtil.write(response,tempFile);
                FileUtil.del(tempFolder);
                FileUtil.del(tempFileName);
            }
            //ServletUtil.setHeader(response,"","");


        }catch (IOException e){
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

    public void onlineEditorDoc(HttpServletRequest request,String filePath){
        Scanner scanner = null;
        FileOutputStream out = null;
        InputStream stream = null;
        HttpURLConnection connection = null;
        try {
            scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
            String body = scanner.hasNext()?scanner.next():"";
            log.info( body );
            if ( StringUtils.isEmpty( body ) ){
                JSONObject jsonObject = JSONUtil.parseObj( body );
                System.out.println( jsonObject );
                /*
                0 - no document with the key identifier could be found,//无法找到具有密钥标识符的文档
                1 - document is being edited,//文件正在编辑中，
                2 - document is ready for saving,//文档准备保存，
                3 - document saving error has occurred,//文件保存错误，
                4 - document is closed with no changes,//文档关闭，没有任何更改，
                6 - document is being edited, but the current document state is saved,//正在编辑文档，但保存了当前文档状态，
                7 - error has occurred while force saving the document.//强制保存文档时发生错误。
                当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，，此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
             * */
                if ( jsonObject.getInt("status") == 2 ){
                    String downloadUri = jsonObject.getStr("url");
                    URL url = new URL(downloadUri);
                    connection = (HttpURLConnection) url.openConnection();
                    stream = connection.getInputStream();
                    File file = new File(filePath);
                    out = new FileOutputStream(file);
                    int read;
                    final byte[] bytes = new byte[1024];
                    while ((read = stream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    out.flush();

                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParameterInvalid(ErrorCode.PLEASE_USE_MULTIPART_UPLOAD);
        }finally {
            if ( scanner !=null ){
                scanner.close();
            }
            try {
                if ( out !=null ){
                    out.close();
                }
                if ( stream !=null ){
                    stream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                throw new ParameterInvalid(ErrorCode.PLEASE_USE_MULTIPART_UPLOAD);
            }
            if ( connection !=null ){
                connection.disconnect();
            }
        }
    }

    public JSONObject getPage(String fileType, String title,String filePath,HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileType",fileType);
        jsonObject.put("key",generateRevisionId(filePath));
        jsonObject.put("title",title);
        jsonObject.put("url",OtherUtils.getUrlPrefix(request) +"/officeFile/" + filePath.replace(gitRootPath,""));
        jsonObject.put("callbackUrl",OtherUtils.getUrlPrefix(request) + "/office/editorCallBack?filepath="+filePath);
        return jsonObject;
    }

    /**
     * 通过 用户id和文件路径混合计算出一个版本号，
     * 版本号相同的情况下，DocumentServer会使用缓存的文档。只有不同时才会重新下载。
     * @param filePath
     * @return
     */
    private String generateRevisionId(String filePath){
        String key = Integer.toString( (filePath).hashCode() );
        return key.substring(0,Math.min(key.length(),20));
    }

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

    public void smallFileUpload(HttpServletRequest request,String savePath,Integer repoId){
        Repos repos = reposMapper.selectById(repoId);
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartFile file =multiRequest.getFile("file");
        if ( file.getSize() > PathConstant.MULTIPART_FILE_THRESHOLD ){
            throw new ParameterInvalid(ErrorCode.PLEASE_USE_MULTIPART_UPLOAD);
        }
        String filePath = savePath + file.getOriginalFilename();
        log.info( "file.getOriginalFilename()---->>"+file.getOriginalFilename() );
        filePath = PathConstant.tranformPathWhenOSIsWindows(filePath);
        File file1 = new File(filePath);
        checkFileIsExistWhenCommonRepo(file1,repos);
        String username = JwtToken.getUsernameByRequest(request);
        checkFileLock(PathConstant.tranformPathWhenOSIsWindows(filePath));
        addFileLock(username,DocLock.TYPE_FILE,filePath);
        try {
            file.transferTo(file1);
        } catch (IOException e) {
            log.info( "上传小文件时发生错误",e );
        }
        checkFileCommit(file1.exists(),filePath,repos);
        //todo 如果是協作仓库，需要在commit后在执行pull
        delFileLock(username,filePath);
    }

    /**
     * 当仓库类型为普通仓库时，如果文件已经存在就终端上传
     * @param file
     * @param repos
     */
    private void checkFileIsExistWhenCommonRepo(File file,Repos repos){
        if ( Objects.equals( repos.getRepoType(),ReposTypeEnums.COMMON_REPO.getType() ) ){
            if ( file.exists() ){
                throw new ParameterInvalid(ErrorCode.FILE_EXIST_BECAUSE_REPOS_IS_COMMON);
            }
        }
    }

    /**
     * 当仓库为版本仓库时需要判断文件是否已经存在，如果存在就要提交
     * @param isExist
     * @param filePath
     * @param repos
     */
    private void checkFileCommit(Boolean isExist,String filePath,Repos repos){
        if ( Objects.equals( repos.getRepoType(),ReposTypeEnums.VERSIONS_REPO.getType() ) ){
            GitUtil.gitAddFile(filePath.replace(repos.getRepoPath(),""),repos.getRepoPath());
            if ( isExist ){
                GitUtil.gitCommit(repos.getRepoPath(),"覆盖上传");
            }else {
                GitUtil.gitCommit(repos.getRepoPath(),"上传");
            }
        }
    }

    public JSONObject multipartUpload(HttpServletRequest request,MultipartParam param ){
        JSONObject j =null;
        switch (param.getPhase()){
            case "start":
                j = MultipartUploadStart(request,param.getSavePath(), param.getName(),param.getRepoId(),param.getSize());
                break;
            case "upload":
                j =MultipartUpload(request,param.getSavePath(),param.getSession_id(),param.getStart_offset());
                break;
            case "finish":
                j = MultipartUploadFinish(request,param.getSavePath(),param.getSession_id(),param.getRepoId(),param.getMd5());
                break;
            default:
                throw new ParameterInvalid(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }
        return j;
    }
    public JSONObject MultipartUploadStart(HttpServletRequest request,String savePath,String fileName,Integer repoId,Long fileSize){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        File file = new File(filePath);
        Repos repos = reposMapper.selectById(repoId);
        checkFileIsExistWhenCommonRepo(file,repos);
        checkFileLock(filePath);
        addFileLock( JwtToken.getUsernameByRequest(request),DocLock.TYPE_FILE,filePath );
        if ( file.exists() ){
            shardToUploadCheckFileIsExist.add( file.getAbsolutePath() );
        }
        RandomAccessFile raf =null;
        try {
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
        JSONObject j2 = new JSONObject();
        j2.put("data",j1);
        j2.put("status","success");
        return j2;
    }
    public JSONObject MultipartUpload(HttpServletRequest request,String savePath,String fileName,Long startOffset){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        JSONObject j = new JSONObject();
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartFile file =multiRequest.getFile("chunk");
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
    public JSONObject MultipartUploadFinish(HttpServletRequest request,String savePath,String fileName,Integer repoId,String fileMd5){
        String filePath = savePath + fileName;
        filePath = PathConstant.tranformPathWhenOSIsWindows( filePath );
        JSONObject j = new JSONObject();
        MD5 md5 = new MD5();
        File file = new File(filePath);
        String m = md5.digestHex(file);
        j.put("status","success");
        if ( openFileSecondPass ){
            FileFingerprint fileFingerprint = new FileFingerprint();
            fileFingerprint.setHash(m);
            fileFingerprint.setFilePath( filePath );
            fileFingerprint.setIsExist(true);
            fileFingerprintMapper.insert( fileFingerprint );
        }
        Repos repos = reposMapper.selectById(repoId);
        checkFileCommit(shardToUploadCheckFileIsExist.contains(file.getAbsolutePath()),filePath,repos);
        shardToUploadCheckFileIsExist.remove( file.getAbsolutePath() );
        delFileLock(JwtToken.getUsernameByRequest(request),filePath);
        return j;
    }

    public void deleteFile(HttpServletRequest request,List<String> filePaths){
        for (String s:filePaths){
            deleteFile(request,s);
        }
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
