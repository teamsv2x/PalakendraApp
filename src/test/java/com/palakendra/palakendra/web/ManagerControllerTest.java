package com.palakendra.palakendra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palakendra.palakendra._testutil.SecurityTestUtils.WithManager;
import com.palakendra.palakendra.domain.entity.CustomerProfile;
import com.palakendra.palakendra.dto.user.CustomerCreateRequest;
import com.palakendra.palakendra.service.ManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManagerController.class)
@ActiveProfiles("test")
class ManagerControllerTest {

    @Resource MockMvc mvc;
    @Resource ObjectMapper om;

    @MockBean ManagerService managerService;

    @Test
    @WithManager
    void createCustomer_returnsResponse() throws Exception {
        var cp = CustomerProfile.builder().id(2L)
                .user(com.palakendra.palakendra.domain.entity.User.builder().phone("9000000001").build())
                .fullName("Ravi").address("Village A").build();

        when(managerService.createOrLinkCustomerToOrg(any(), any(), any(), any())).thenReturn(cp);

        var req = new CustomerCreateRequest("Ravi", "9000000001", "Village A");

        mvc.perform(post("/api/manager/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId").value(2))
                .andExpect(jsonPath("$.data.phone").value("9000000001"));
    }
}
