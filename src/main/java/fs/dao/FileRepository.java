package fs.dao;

import fs.domain.FileItem;
import fs.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<FileItem, Long> {
    List<FileItem> findAllByOwner(User user);
}
