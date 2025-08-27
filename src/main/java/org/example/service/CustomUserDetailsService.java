package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.example.repository.UserRepository;
import org.example.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}