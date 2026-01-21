package com.win777.backend.controller;

import com.win777.backend.dto.ConfigRequest;
import com.win777.backend.entity.AppConfig;
import com.win777.backend.entity.Banner;
import com.win777.backend.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigurationController {
    
    @Autowired
    private ConfigurationService configurationService;
    
    /**
     * Get configuration value by key (public endpoint for mobile app)
     */
    @GetMapping("/{key}")
    public ResponseEntity<?> getConfig(@PathVariable String key) {
        String value = configurationService.getConfigValue(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(value);
    }
    
    /**
     * Get all active configurations (public endpoint)
     */
    @GetMapping
    public ResponseEntity<List<AppConfig>> getAllConfigs() {
        return ResponseEntity.ok(configurationService.getAllActiveConfigs());
    }
    
    /**
     * Check maintenance mode (public endpoint)
     */
    @GetMapping("/maintenance-mode")
    public ResponseEntity<?> getMaintenanceMode() {
        boolean isMaintenanceMode = configurationService.isMaintenanceMode();
        return ResponseEntity.ok().body(isMaintenanceMode);
    }
    
    /**
     * Get theme color (public endpoint)
     */
    @GetMapping("/theme-color")
    public ResponseEntity<?> getThemeColor() {
        String color = configurationService.getThemeColor();
        return ResponseEntity.ok().body(color);
    }
    
    /**
     * Get active banners (public endpoint)
     */
    @GetMapping("/banners")
    public ResponseEntity<List<Banner>> getBanners() {
        return ResponseEntity.ok(configurationService.getActiveBanners());
    }
    
    /**
     * Set configuration value (admin only)
     */
    @PostMapping
    public ResponseEntity<AppConfig> setConfig(@RequestBody ConfigRequest request) {
        AppConfig config = configurationService.setConfigValue(
            request.getConfigKey(), 
            request.getConfigValue(), 
            request.getConfigType(), 
            request.getDescription()
        );
        return ResponseEntity.ok(config);
    }
    
    /**
     * Set maintenance mode (admin only)
     */
    @PostMapping("/maintenance-mode")
    public ResponseEntity<?> setMaintenanceMode(@RequestBody boolean enabled) {
        configurationService.setMaintenanceMode(enabled);
        return ResponseEntity.ok("Maintenance mode updated");
    }
    
    /**
     * Set theme color (admin only)
     */
    @PostMapping("/theme-color")
    public ResponseEntity<?> setThemeColor(@RequestBody String color) {
        configurationService.setThemeColor(color);
        return ResponseEntity.ok("Theme color updated");
    }
    
    /**
     * Create or update banner (admin only)
     */
    @PostMapping("/banners")
    public ResponseEntity<Banner> saveBanner(@RequestBody Banner banner) {
        Banner savedBanner = configurationService.saveBanner(banner);
        return ResponseEntity.ok(savedBanner);
    }
    
    /**
     * Delete banner (admin only)
     */
    @DeleteMapping("/banners/{bannerId}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long bannerId) {
        configurationService.deleteBanner(bannerId);
        return ResponseEntity.ok("Banner deleted");
    }
    
    /**
     * Clear configuration cache (admin only)
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<?> clearCache() {
        configurationService.clearConfigCache();
        return ResponseEntity.ok("Cache cleared");
    }
}
