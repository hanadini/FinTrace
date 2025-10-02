package com.FinTrace.customerService.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserControl {
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public String getUserInfo() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "User info for: " + username;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminInfo() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "Admin info for: " + username;
    }

    @GetMapping("/deposit")
    @PreAuthorize("hasRole('DEPOSIT')")
    public String getDepositInfo() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "Deposit Info: " + username;
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String getCustomerInfo() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "Customer Info: " + username;
    }

    @GetMapping("/whoami")
    @PreAuthorize("isAuthenticated()")
    public String whoAmI(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "You are authenticated as: " + authentication.getAuthorities();
        } else {
            return "You are not authenticated.";
        }
    }
}
