package org.worker.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.worker.repository.Implementation.UsersRepoService;
import org.worker.repository.UsersRepository;
import org.worker.user.Role;
import org.worker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceFromJson implements UserDetailsService {


    @Autowired
    private final UsersRepository usersRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> foundUser = usersRepo.findByEmail(email);

        List<String> roles = foundUser
                .get()
                .getRoles()
                .stream()
                .map(Enum::toString)
                .toList();

        return foundUser.map(user ->
                        org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .roles(String.valueOf(roles))
                                .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }

}
