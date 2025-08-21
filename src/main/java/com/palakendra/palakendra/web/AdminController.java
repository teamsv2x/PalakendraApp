package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.dto.user.ManagerCreateRequest;
import com.palakendra.palakendra.dto.user.ManagerResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository users; private final OrganizationRepository orgs; private final PasswordEncoder encoder;
    public AdminController(UserRepository users, OrganizationRepository orgs, PasswordEncoder encoder){this.users=users; this.orgs=orgs; this.encoder=encoder;}

    @PostMapping("/managers")
    public ApiResponse<ManagerResponse> createManager(@Valid @RequestBody ManagerCreateRequest req){
        var manager = User.builder().username(req.username()).email(req.email()).password(encoder.encode(req.password())).role(Role.MANAGER).active(true).build();
        users.save(manager);
        var org = Organization.builder().name(req.organizationName()).address(req.address()).manager(manager).build();
        orgs.save(org);
        return ApiResponse.ok(new ManagerResponse(manager.getId(), org.getId(), manager.getUsername(), org.getName()));
    }

    @DeleteMapping("/managers/{userId}")
    public ApiResponse<Void> deleteManager(@PathVariable Long userId){
        users.deleteById(userId);
        return ApiResponse.ok();
    }

    @GetMapping("/managers")
    public ApiResponse<List<User>> listManagers(){
        return ApiResponse.ok(users.findAll().stream().filter(u -> u.getRole()== Role.MANAGER).toList());
    }
}
