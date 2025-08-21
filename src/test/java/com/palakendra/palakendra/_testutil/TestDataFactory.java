package com.palakendra.palakendra._testutil;

import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.Role;

public class TestDataFactory {
    public static User admin(String username) {
        return User.builder().username(username).email(username+"@ex.com")
                .role(Role.ADMIN).active(true).password("$2a$10$S9p6Y7Dk3JtH.rSkUXbXkOfdYQO2G2jOAGkTQOdsuP8Rx0NwvYe3i") // admin@123
                .build();
    }
    public static User manager(String u) {
        return User.builder().username(u).email(u+"@ex.com").role(Role.MANAGER).active(true).password("$2a$10$S9p6Y7Dk3JtH.rSkUXbXkOfdYQO2G2jOAGkTQOdsuP8Rx0NwvYe3i").build();
    }
    public static User customerUser(String phone) {
        return User.builder().username(phone).phone(phone).role(Role.CUSTOMER).active(true).build();
    }
    public static CustomerProfile customerProfile(User u, String name, String addr) {
        return CustomerProfile.builder().user(u).fullName(name).address(addr).build();
    }
}
