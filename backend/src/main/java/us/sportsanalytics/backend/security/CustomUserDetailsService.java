package us.sportsanalytics.backend.security;

import java.util.UUID;

// import java.util.Collection;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
// import us.sportsanalytics.backend.models.domain.Role;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.User;
import us.sportsanalytics.backend.repositories.user.UserRepository;
import us.sportsanalytics.utility.EmailChecker;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User user;

        if (EmailChecker.isValid(identifier)) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        }

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: id " + id.toString()));

        return new CustomUserDetails(user);
    }
}
