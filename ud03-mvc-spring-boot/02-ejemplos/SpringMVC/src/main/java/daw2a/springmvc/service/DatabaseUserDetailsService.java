package daw2a.springmvc.service;

import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public DatabaseUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) {
        User user = users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()).roles("USER").build();
    }
}
