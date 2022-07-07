package com.niubaide.im.service;

import com.niubaide.im.model.vo.ResponseJson;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FileUploadService {
    ResponseJson upload(MultipartFile file, HttpServletRequest request);

}
