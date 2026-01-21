package com.win777.backend.controller;

import com.win777.backend.entity.Brand;
import com.win777.backend.entity.BrandConfig;
import com.win777.backend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for brand management (white-label support)
 */
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    /**
     * Get brand by code
     */
    @GetMapping("/{brandCode}")
    public ResponseEntity<Brand> getBrandByCode(@PathVariable String brandCode) {
        return brandService.getBrandByCode(brandCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get brand by domain
     */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<Brand> getBrandByDomain(@PathVariable String domain) {
        return brandService.getBrandByDomain(domain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active brands
     */
    @GetMapping
    public ResponseEntity<List<Brand>> getAllActiveBrands() {
        List<Brand> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(brands);
    }

    /**
     * Get brand configuration
     */
    @GetMapping("/{brandCode}/config/{configKey}")
    public ResponseEntity<String> getBrandConfig(@PathVariable String brandCode, @PathVariable String configKey) {
        return brandService.getBrandByCode(brandCode)
                .flatMap(brand -> brandService.getBrandConfig(brand, configKey))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all brand configurations
     */
    @GetMapping("/{brandCode}/config")
    public ResponseEntity<Map<String, String>> getAllBrandConfigs(@PathVariable String brandCode) {
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    Map<String, String> configs = brandService.getAllBrandConfigs(brand);
                    return ResponseEntity.ok(configs);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new brand (Admin only)
     */
    @PostMapping("/admin/create")
    public ResponseEntity<?> createBrand(
            @RequestParam String brandCode,
            @RequestParam String brandName,
            @RequestParam String domain) {
        try {
            Brand brand = brandService.createBrand(brandCode, brandName, domain);
            return ResponseEntity.ok(brand);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Set brand configuration (Admin only)
     */
    @PostMapping("/admin/{brandCode}/config")
    public ResponseEntity<?> setBrandConfig(
            @PathVariable String brandCode,
            @RequestParam String configKey,
            @RequestParam String configValue,
            @RequestParam(defaultValue = "STRING") String configType,
            @RequestParam(required = false) String description) {
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    BrandConfig config = brandService.setBrandConfig(brand, configKey, configValue, configType, description);
                    return ResponseEntity.ok(config);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete brand configuration (Admin only)
     */
    @DeleteMapping("/admin/{brandCode}/config/{configKey}")
    public ResponseEntity<?> deleteBrandConfig(@PathVariable String brandCode, @PathVariable String configKey) {
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    brandService.deleteBrandConfig(brand, configKey);
                    return ResponseEntity.ok().body("Configuration deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
