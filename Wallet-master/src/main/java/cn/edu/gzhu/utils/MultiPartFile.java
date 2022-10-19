package cn.edu.gzhu.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

public class MultiPartFile {

    public static String uploadPath;

    public static boolean uploadFile(MultipartFile file, String fileName) {
        if (file == null) return false;
        if (fileName == null) fileName = file.getOriginalFilename();
        try {
            file.transferTo(new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    //缓存数据到文件
    public static boolean uploadDataToFile(String fileName, Map<String, Object> map) {
        uploadPath = "D:/";
        File file = new File(uploadPath + fileName);
        FileWriter fw = null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            fw = new FileWriter(uploadPath+"transaction.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            JSONObject object = new JSONObject(map);
            String jsonStr = object.toString();
            bw.write(jsonStr);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void download(String path, HttpServletResponse response) throws Exception {
        String fileName = null;
        FileInputStream fis = null;
        File file = new File(uploadPath + path);
        if (path.contains("/")) fileName = path.substring(path.lastIndexOf("/"), path.length());
        else fileName = path;//如果文件在根路径
        if (!file.exists()) {
            errorPath("系统找不到指定的路径", response);
            return;
        }
        if (file.isDirectory()) {
            errorPath("文件夹不可下载", response);
            return;
        }
        //附件形式下载
        response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("form/data;charset=utf-8");
        FileCopyUtils.copy(fis, response.getOutputStream());
        response.flushBuffer();
    }


    public static void errorPath(String message, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write(message);
    }
}