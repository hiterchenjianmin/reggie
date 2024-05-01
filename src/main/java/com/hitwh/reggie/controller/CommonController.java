package com.hitwh.reggie.controller;

import com.hitwh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //使用uuid来重新写文件名，防止名称重复造成文件覆盖
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+suffix;

        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            dir.mkdirs();
        }
        file.transferTo(new File(basePath+fileName));

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int length =0;
            byte[] bytes =new byte[1024];
            while((length=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
