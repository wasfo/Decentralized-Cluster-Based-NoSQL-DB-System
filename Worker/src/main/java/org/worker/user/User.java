package org.worker.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    //private long id;
    private String username;
    private String password;
    private String affinity;
    private List<Role> roles;
}
