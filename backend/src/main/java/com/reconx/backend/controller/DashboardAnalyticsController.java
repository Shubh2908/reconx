package com.reconx.backend.controller;

import com.reconx.backend.dto.DashboardAnalyticsResponse;
import com.reconx.backend.service.DashboardAnalyticsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService dashboardAnalyticsService;

    public DashboardAnalyticsController(
            DashboardAnalyticsService dashboardAnalyticsService) {
        this.dashboardAnalyticsService = dashboardAnalyticsService;
    }

    @GetMapping("/analytics")
    public DashboardAnalyticsResponse getDashboardAnalytics() {
        return dashboardAnalyticsService.getDashboardAnalytics();
    }
}