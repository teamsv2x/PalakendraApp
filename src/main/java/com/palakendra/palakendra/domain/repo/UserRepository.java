package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(Role role);

    Optional<? extends User> findByPhone(String phone);

    boolean existsByPhone(String phone);

}

