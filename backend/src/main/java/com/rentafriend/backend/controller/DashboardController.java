package com.rentafriend.backend.controller;

import com.rentafriend.backend.dto.DashboardResponse;
import com.rentafriend.backend.model.UserAccount;
import com.rentafriend.backend.service.DashboardService;
import com.rentafriend.backend.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SessionService sessionService;

    public DashboardController(DashboardService dashboardService, SessionService sessionService) {
        this.dashboardService = dashboardService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@RequestHeader("Authorization") String authorization) {
        UserAccount user = sessionService.requireUser(authorization);
        return dashboardService.getDashboard(user);
    }
}

