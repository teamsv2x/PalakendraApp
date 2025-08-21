// src/test/java/com/palakendra/palakendra/web/ManagerControllerMoreTest.java
package com.palakendra.palakendra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palakendra.palakendra._testutil.SecurityTestUtils.WithManager;
import com.palakendra.palakendra.config.SecurityConfig;
import com.palakendra.palakendra.security.JwtAuthFilter;
import com.palakendra.palakendra.service.ManagerService;
import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import com.palakendra.palakendra.dto.user.CustomerBlockRequest;
import com.palakendra.palakendra.dto.user.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = ManagerController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ManagerControllerMoreTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean ManagerService managerService;

    @Test @WithMockUser(username = "mgrA", roles = "MANAGER")
    void updateCustomer_changes_name_address_phone() throws Exception {
        var cp = CustomerProfile.builder()
                .id(2L)
                .user(User.builder().phone("9000000022").build())
                .fullName("Ravi Kumar").address("Village B").build();

        when(managerService.updateCustomerInMyOrg(eq("mgrA"), eq(2L), anyString(), anyString(), anyString()))
                .thenReturn(cp);

        var req = new CustomerUpdateRequest("Ravi Kumar", "Village B", "9000000022");

        mvc.perform(put("/api/manager/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.customerId").value(2))
                .andExpect(jsonPath("$.data.phone").value("9000000022"))
                .andExpect(jsonPath("$.data.fullName").value("Ravi Kumar"));
    }

    @Test @WithMockUser(username = "mgrA", roles = "MANAGER")
    void block_and_list_blocked() throws Exception {
        doNothing().when(managerService).blockCustomerInMyOrg(eq("mgrA"), eq(2L), any());

        mvc.perform(patch("/api/manager/customers/2/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new CustomerBlockRequest("moved"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        var co = CustomerOrganization.builder()
                .customer(CustomerProfile.builder()
                        .id(2L)
                        .fullName("Ravi")
                        .user(User.builder().phone("9000000001").build())
                        .address("Village A").build())
                .status(CustomerOrgStatus.BLOCKED)
                .build();

        when(managerService.listMyCustomers("mgrA", CustomerOrgStatus.BLOCKED))
                .thenReturn(List.of(co));

        mvc.perform(get("/api/manager/customers?status=BLOCKED"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value("BLOCKED"));
    }
}
