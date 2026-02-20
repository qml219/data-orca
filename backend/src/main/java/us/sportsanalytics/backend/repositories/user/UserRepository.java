package us.sportsanalytics.backend.repositories.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import us.sportsanalytics.backend.models.domain.User;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
