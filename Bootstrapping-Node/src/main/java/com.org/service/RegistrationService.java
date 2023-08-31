package com.org.service;

import com.org.user.User;
import org.springframework.http.ResponseEntity;

public interface RegistrationService {

    public boolean registerUser(String username, String password);
    public ResponseEntity<String> registerUserInNodes(User user);

}
