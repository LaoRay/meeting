package com.clubank.meeting.service;

import com.clubank.meeting.common.ApiResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService {

    /**
     * 上传文件
     */
    ApiResult uploadFile(MultipartFile file);

    /**
     * 下载文件
     */
    void downloadFile(HttpServletResponse response, String date, String newFileName, String originalFilename);
}
