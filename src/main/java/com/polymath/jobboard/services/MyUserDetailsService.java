package com.polymath.jobboard.services;

import com.polymath.jobboard.models.UserPrincipal;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.UsersRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepositories usersRepositories;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users email = usersRepositories.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserPrincipal(email);
    }
}
