package com.gk.study.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RestController
@RequestMapping("/staticfiles")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @RequestMapping("/image/{name}")
    public void image(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
        String filePath = uploadPath + File.separator + "image" + File.separator + name;
        //1、设置response 响应头
        response.reset(); //设置页面不缓存,清空buffer
        response.setCharacterEncoding("UTF-8"); //字符编码
        response.setContentType("multipart/form-data"); //二进制传输数据
        //设置响应头
        response.setHeader("Content-Disposition",
                "attachment;fileName="+ URLEncoder.encode(name, "UTF-8"));
        File file = new File(filePath);
        try(        InputStream input=new FileInputStream(file);
                    //3、 写出文件--输出流
                    OutputStream out = response.getOutputStream()){

            byte[] buff =new byte[1024];
            int index=0;
            //4、执行 写出操作
            while((index= input.read(buff))!= -1){
                out.write(buff, 0, index);
                out.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/avatar/{name}")
    public void avatar(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
        String filePath = uploadPath + File.separator + "avatar" + File.separator + name;
        //1、设置response 响应头
        response.reset(); //设置页面不缓存,清空buffer
        response.setCharacterEncoding("UTF-8"); //字符编码
        response.setContentType("multipart/form-data"); //二进制传输数据
        //设置响应头
        response.setHeader("Content-Disposition",
                "attachment;fileName="+ URLEncoder.encode(name, "UTF-8"));
        File file = new File(filePath);
        try(        InputStream input=new FileInputStream(file);
                    //3、 写出文件--输出流
                    OutputStream out = response.getOutputStream()){

            byte[] buff =new byte[1024];
            int index=0;
            //4、执行 写出操作
            while((index= input.read(buff))!= -1){
                out.write(buff, 0, index);
                out.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
