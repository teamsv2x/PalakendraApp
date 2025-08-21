package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByManager(User manager);

    // Explicit JPQL to avoid any nested-property parsing issues
    @Query("select o from Organization o where o.manager.id = :managerId")
    Optional<Organization> findByManagerId(@Param("managerId") Long managerId);
}

