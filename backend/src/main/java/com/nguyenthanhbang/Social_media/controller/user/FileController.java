package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.FileResponse;
import com.nguyenthanhbang.Social_media.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;
    @PostMapping
    public ResponseEntity<ApiResponse<List<FileResponse>>> uploadFile(@RequestParam(name = "files", required = false) MultipartFile[] files, @RequestParam String folder) throws IOException {
        List<FileResponse> fileResponses = new ArrayList<>();
        for(int i=0; i<files.length; i++) {
            String fileName = fileService.uploadFile(files[i], folder);
            FileResponse response = FileResponse.builder()
                    .uploadOrder(i+1)
                    .mediaType(fileService.getMediaType(files[i].getOriginalFilename().toLowerCase()))
                    .mediaUrl(fileName)
                    .build();
            fileResponses.add(response);
        }
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Upload file successfully")
                .data(fileResponses)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
