package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.enumeration.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile file, String folder) throws IOException;
    MediaType getMediaType(String filename);
}
