package fs.service;

import fs.domain.FileItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface StorageService {
    void init();

    /**
     * @param file uploaded file
     * @return
     * @throws IllegalArgumentException - if we can't create folder for storing file RuntimeException
     *                                  - if we can't store file to the FS
     */
    FileItem store(MultipartFile file);

    List<FileItem> loadAll();

    /**
     * @param fileId
     * @return
     * @throws IllegalArgumentException - if file didn't exists into the DB AccessDeniedException - if
     *                                  current logged user doesn't have access to this file
     */
    FileItem load(Long fileId);

    // We don't need this methods at the moment related to the TASK!.
    // Resource loadAsResource(String filename);
    // void deleteAll();
}
