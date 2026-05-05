package com.foodapp.restaurant;

import com.foodapp.admin.dto.VendorAnalyticsDto;
import com.foodapp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorAnalyticsController {

    private final VendorAnalyticsService analyticsService;

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<VendorAnalyticsDto>> getAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        VendorAnalyticsDto analytics = analyticsService.getAnalytics(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }
}
