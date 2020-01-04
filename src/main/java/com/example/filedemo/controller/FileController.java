package com.example.filedemo.controller;

import com.example.filedemo.exception.InvalidPathException;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/file-demo/v1")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    //@PreAuthorize("oauth2.hasScope('apiWriteScope')")
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String path,
                                         @RequestParam String clientName) {
        logger.info(">>>>>>>> uploading the file : {},clientName : {}",path,clientName);

        long startTime = System.currentTimeMillis();

        String fileName = fileStorageService.storeFile(file,getPathLocation(Optional.ofNullable(path)));

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    private Path getPathLocation(Optional<String> path) {

        return Paths.get(path.filter(p-> !p.isEmpty()).orElseThrow(() -> new InvalidPathException("path is required " +
                                                                                                          "field"))).toAbsolutePath().normalize();
    }
   //@PreAuthorize("oauth2.hasScope('apiWriteScope')")
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam String path,
                                                        @RequestParam String clientName) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file,path,clientName))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{path}/{clientName}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,@PathVariable String path,
                                                 @PathVariable String clientName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName,getPathLocation(Optional.ofNullable(path)));

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
