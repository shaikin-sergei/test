package fs.dao;

import fs.domain.User;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Long> {
    User findByUsername(String username);

    User findById(Long id);
}
