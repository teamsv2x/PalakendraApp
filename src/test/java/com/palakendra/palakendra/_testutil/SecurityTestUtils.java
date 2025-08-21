package com.palakendra.palakendra._testutil;

import org.springframework.security.test.context.support.WithMockUser;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SecurityTestUtils {
    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public @interface WithAdmin {}

    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(username = "mgrA", roles = {"MANAGER"})
    public @interface WithManager {}

    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(username = "9000000001", roles = {"CUSTOMER"})
    public @interface WithCustomer {}
}
