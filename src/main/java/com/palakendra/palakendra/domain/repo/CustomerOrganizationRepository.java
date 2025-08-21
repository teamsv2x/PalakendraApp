package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.CustomerOrganization;
import com.palakendra.palakendra.domain.entity.CustomerProfile;
import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;
public interface CustomerOrganizationRepository extends JpaRepository<CustomerOrganization, Long> {
    Optional<CustomerOrganization> findByCustomerIdAndOrganizationId(Long customerId, Long orgId);
    List<CustomerOrganization> findAllByCustomer(CustomerProfile customer);
    List<CustomerOrganization> findAllByOrganization(Organization org);

    List<CustomerOrganization> findAllByOrganizationAndStatus(Organization org, CustomerOrgStatus status);
    long countByCustomer(CustomerProfile customer);
}
