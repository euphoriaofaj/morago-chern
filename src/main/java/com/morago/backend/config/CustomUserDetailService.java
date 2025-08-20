package com.morago.backend.config;

import com.morago.backend.entity.User;
import com.morago.backend.exception.UserNotFoundException;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> new org.springframework.security.core.userdetails.User(
                        u.getUsername(),
                        u.getPassword(),
                        u.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}



//public class CustomUserDetailService implements UserDetailsService {
//    private final UserService userService;
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        try {
//            User u = userService.findByUsernameOrThrow(username);
//            return new org.springframework.security.core.userdetails.User(
//                    u.getUsername(),
//                    u.getPassword(),
//                    u.getRoles().stream()
//                            .map(r -> new SimpleGrantedAuthority(r.getName().name()))
//                            .collect(Collectors.toList())
//            );
//        } catch (UserNotFoundException ex) {
//            throw new UsernameNotFoundException(ex.getMessage());
//        }
//    }
//}
