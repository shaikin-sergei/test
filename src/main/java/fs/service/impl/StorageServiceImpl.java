package fs.service.impl;

import fs.dao.FileRepository;
import fs.dao.UserRepository;
import fs.domain.FileItem;
import fs.domain.User;
import fs.security.UserDetails;
import fs.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class StorageServiceImpl implements StorageService {
    private static final String FILENAME_PATTERN = "%s" + File.separator + "%s";
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Value("${file_store.path.root}")
    private String storePath;

    public StorageServiceImpl(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @PostConstruct
    public void init() {
        if (storePath == null || storePath.isEmpty()) {
            throw new IllegalArgumentException("Please provide STORE_PATH for correctly initialize StoreService!");
        }
        File storePathRoot = new File(storePath);
        if (storePathRoot.mkdirs()) {
            log.info("Create folder: \"{}\"", storePathRoot.getAbsolutePath());
        }
        if (!storePathRoot.exists())
            throw new IllegalArgumentException("Provided path: " + storePath + " doesn't exists!");
        if (!storePathRoot.isDirectory())
            throw new IllegalArgumentException("Provided path: " + storePath + " isn't a directory!");
    }

    private UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String getFullPathForStore() {
        return String.format(FILENAME_PATTERN, storePath, getCurrentUser().getUserId());
    }

    private User getOwner(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId);
        if (currentUser == null) {
            throw new IllegalArgumentException("Can't load current user details from DB. Please check!");
        }
        return currentUser;
    }

    @Override
    @Transactional
    public FileItem store(MultipartFile multipart) {
        try {
            String fileName = UUID.randomUUID().toString(),
                    fullFilePath = getFullPathForStore(),
                    fullFileName = fullFilePath + File.separator + fileName;

            User currentUser = getOwner(getCurrentUser().getUserId());
            File folder = new File(fullFilePath);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IllegalArgumentException("Can't create folder for storing user files. Path: " + fullFilePath);
            }

            FileItem fileItem = new FileItem();
            File file = new File(fullFileName);
            File dir = file.getParentFile();
            if (dir.mkdirs()) {
                log.info("Create directory \"{}\"", dir.getAbsolutePath());
            }

            multipart.transferTo(file);
            fileItem.setName(multipart.getOriginalFilename());
            fileItem.setFsPath(fullFileName);
            fileItem.setOwner(currentUser);
            return fileRepository.save(fileItem);
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException("Can't store file", e);
        }
    }

    @Override
    @Transactional
    public List<FileItem> loadAll() {
        return fileRepository.findAllByOwner(getOwner(getCurrentUser().getUserId()));
    }

    @Override
    public FileItem load(Long fileId) {
        User currentUser = getOwner(getCurrentUser().getUserId());

        Optional<FileItem> fileItem = fileRepository.findById(fileId);
        if (!fileItem.isPresent()) {
            throw new IllegalArgumentException("Can't found requested file. Please check!");
        }
        if (!fileItem.get().getOwner().equals(currentUser)) {
            throw new AccessDeniedException("You haven't access to this file. Please try another one");
        }
        return fileItem.get();
    }
}
