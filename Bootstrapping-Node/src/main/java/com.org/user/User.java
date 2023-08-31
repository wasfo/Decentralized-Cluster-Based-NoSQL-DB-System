package com.org.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private String username;
    private String password;
    private String affinity;
    private List<Role> role;
}
