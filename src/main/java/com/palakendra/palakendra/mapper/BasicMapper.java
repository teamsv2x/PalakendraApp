package com.palakendra.palakendra.mapper;

import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.dto.org.OrganizationResponse;

public class BasicMapper {
    public static OrganizationResponse toDto(Organization o) {
        if (o == null) return null;
        return new OrganizationResponse(o.getId(), o.getName(), o.getAddress());
    }
}
