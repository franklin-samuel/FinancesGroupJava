package com.metas.meta_financeira.repositories;

import java.util.Optional;
import com.metas.meta_financeira.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRespository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthId(String oauthId);
}
