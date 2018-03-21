package com.clubank.meeting.controller;

import com.clubank.meeting.common.ApiResult;
import com.clubank.meeting.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传下载
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @ResponseBody
    public ApiResult uploadFile(@RequestParam(value = "file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    /**
     * 文件下载
     *
     * @return
     */
    @GetMapping("/{date}/{newFileName}/{originalFilename}")
    public void downloadFile(@PathVariable("date") String date, @PathVariable("newFileName") String newFileName, @PathVariable("originalFilename") String originalFilename, HttpServletResponse response) {
        fileService.downloadFile(response, date, newFileName, originalFilename);
    }
}
