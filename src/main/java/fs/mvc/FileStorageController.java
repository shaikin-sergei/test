package fs.mvc;

import fs.domain.FileItem;
import fs.mvc.dto.FileItemDTO;
import fs.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/fileStorage")
public class FileStorageController {
    private static final String PROFILE_URL = "/profile";

    private final StorageService storageService;

    @Autowired
    public FileStorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/all")
    public List<FileItemDTO> getAll() {
        List<FileItem> files = storageService.loadAll();
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .map(file -> new FileItemDTO(file.getId(), file.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping("/uploadFile")
    public void uploadFile(@RequestParam("uploadFile") MultipartFile file, HttpServletResponse response)
            throws IOException {
        storageService.store(file);
        response.sendRedirect(PROFILE_URL);
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        FileItem fileItem;
        try {
            fileItem = storageService.load(fileId);
        } catch (AccessDeniedException err) {
            response.sendError(FORBIDDEN.value());
            return null;
        } catch (IllegalArgumentException e) {
            response.sendError(NOT_FOUND.value());
            return null;
        }

        String contentType = request.getServletContext().getMimeType(fileItem.getName());
        if (contentType == null) {
            contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }

        Path filePath = Paths.get(fileItem.getFsPath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            response.sendError(NOT_FOUND.value());
            return null;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileItem.getName() + "\"")
                .body(new UrlResource(filePath.toUri()));
    }
}
