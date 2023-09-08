package org.worker.repository;

import org.worker.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



public interface UsersRepository {

    public Optional<User> findByEmail(String email);

    public List<User> readUsers();

    public Boolean save(User user);

    public Boolean deleteByEmail(String email);

}
