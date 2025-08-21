package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.CustomerProfile;
import com.palakendra.palakendra.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUser(User user);
}
