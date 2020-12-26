package com.vesystem.version;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @auther hcy
 * @create 2020-09-01 15:01
 * @Description
 */
public class ApiTest {

    public static void main(String[] args) throws IOException, GitAPIException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("language");
            root.setAttribute("cat", "it");
            Element lan1 = document.createElement("lan");
            lan1.setAttribute("id" , "1");
            Element name1 = document.createElement("name");
            name1.setTextContent("java");
            Element ide1 = document.createElement("ide");
            ide1.setTextContent("myeclipse");

            lan1.appendChild(name1);
            lan1.appendChild(ide1);
            root.appendChild(lan1);

            document.appendChild(root);

            //创建转换工厂，然后将创建的document转换输出到文件中或控制台
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(new File("newXml.xml")));
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            System.out.println(stringWriter.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getDayStr(Date startDay,Date endDay,String sdfStr){
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(sdfStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDay);
        do {
            list.add( sdf.format(calendar.getTime()) );
            calendar.add(Calendar.DAY_OF_YEAR,1);
        }while (calendar.getTime().before( endDay ));
        return list;
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
