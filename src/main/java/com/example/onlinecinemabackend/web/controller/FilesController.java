package com.example.onlinecinemabackend.web.controller;
import java.util.List;
import java.util.stream.Collectors;

import com.example.onlinecinemabackend.service.FilesStorageService;
import com.example.onlinecinemabackend.web.dto.response.ActorResponse;
import com.example.onlinecinemabackend.web.dto.response.FileInfoResponse;
import com.example.onlinecinemabackend.web.dto.response.MessageResponse;
import com.example.onlinecinemabackend.web.dto.response.ModelListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/files")
public class FilesController {

    private final FilesStorageService storageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public ResponseEntity<FileInfoResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        storageService.save(file);
        String filename = file.getOriginalFilename();
        String url = MvcUriComponentsBuilder.fromMethodName(FilesController.class, "getFile", file.getOriginalFilename()).build().toString();
        FileInfoResponse fileInfo = new FileInfoResponse(filename, url);
        return ResponseEntity.ok().body(fileInfo);
    }

    @GetMapping
    public ResponseEntity<ModelListResponse<FileInfoResponse>> getListFiles() {
        List<FileInfoResponse> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfoResponse(filename, url);
        }).collect(Collectors.toList());
        return  ResponseEntity.ok(
                ModelListResponse.<FileInfoResponse>builder()
                        .totalCount((long) fileInfos.size())
                        .data(fileInfos)
                        .build()
        );
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public ResponseEntity<MessageResponse> deleteFile(@PathVariable String filename) {
        String message = "";

        try {
            boolean existed = storageService.delete(filename);

            if (existed) {
                message = "Delete the file successfully: " + filename;
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            }

            message = "The file does not exist!";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(message));
        }
    }
}