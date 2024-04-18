package com.semmtech.laces.fetch.security.service;

import com.semmtech.laces.fetch.security.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The user details are stored in mongodb in collection users,
 * the only thing we use for now is a username and password.
 */
@Component
public class MongoUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public MongoUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(lacesFetchUser -> new User(
                        lacesFetchUser.getUsername(),
                        lacesFetchUser.getPassword(),
                        Arrays.asList(new SimpleGrantedAuthority("user"))
                )).orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found."));
    }
}
