package com.clubank.meeting.service.impl;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.common.ApiStatusCode;
import com.clubank.meeting.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件上传下载
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${file.root.path}")
    private String fileRootPath;

    @Override
    public ApiResult uploadFile(MultipartFile file) {
        log.info("上传文件：{}", file.getName());
        try {
            StringBuffer path = new StringBuffer(fileRootPath); //根路径
            path.append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append("/");
            return uploadFile(file, path.toString());
        } catch (Exception e) {
            log.error("服务器内部错误，上传文件失败", e);
            return new ApiResult(ApiStatusCode.INNER_SERVER_ERROR.getValue(), ApiStatusCode.INNER_SERVER_ERROR.toString());
        }
    }

    /**
     * 保存文件
     *
     * @param file
     * @param path
     * @return
     * @throws Exception
     */
    private ApiResult uploadFile(MultipartFile file, String path) throws Exception {
        if (file == null) {
            return new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), ApiStatusCode.PARRAM_ERROR.toString());
        }
        String originalFilename = file.getOriginalFilename();// 获得源文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();// 获得文件后缀

        // 判断上传的文件格式
        if (!suffix.matches("^[(jpg)|(bmp)|(png)|(gif)|(rar)|(zip)|(doc)|(docx)|(pdf)|(ppt)|(pptx)|(xls)|(xlsx)]+$")) {
            new ApiResult(ApiStatusCode.PARRAM_ERROR.getValue(), "请上传(jpg)|(bmp)|(png)|(gif)|(rar)|(zip)|(doc)|(docx)|(pdf)|(ppt)|(pptx)|(xls)|(xlsx)格式的文件");
        }
        String newFileName = new Date().getTime() + "." + suffix; //新文件名
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, newFileName);
        // MultipartFile自带的解析方法 转存文件
        file.transferTo(dest);
        log.info("文件上传成功：{}", path + newFileName);
        return new ApiResult(ApiStatusCode.SUCCESS.getValue(), ApiStatusCode.SUCCESS.toString(), (path + newFileName).replace(fileRootPath, ""));
    }

    @Override
    public void downloadFile(HttpServletResponse response, String date, String newFileName, String originalFilename) {
        BufferedInputStream bis = null;
        BufferedOutputStream out = null;
        String path = date + "/" + newFileName;
        log.info("下载文件：" + path);
        try {
            //获取输入流
            bis = new BufferedInputStream(new FileInputStream(new File(fileRootPath + path)));
            //转码，免得文件名中文乱码
            originalFilename = URLEncoder.encode(originalFilename, "UTF-8");
            //设置文件下载头
            response.addHeader("Content-Disposition", "attachment;filename=" + originalFilename + newFileName.substring(newFileName.lastIndexOf(".")));
            //设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            while ((len = bis.read()) != -1) {
                out.write(len);
            }
        } catch (Exception e) {
            log.error("文件未找到：" + path);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
