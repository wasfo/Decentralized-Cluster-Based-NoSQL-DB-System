package org.worker.repository;
import org.worker.user.User;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public interface UsersRepository {

    public Optional<User> findByEmail(String email);
    public  Boolean save(User user);
    public  Boolean deleteByEmail(String email);

}
