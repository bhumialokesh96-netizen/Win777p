package com.win777.backend.service;

import com.win777.backend.entity.Brand;
import com.win777.backend.entity.BrandConfig;
import com.win777.backend.repository.BrandRepository;
import com.win777.backend.repository.BrandConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing brands and brand configurations (white-label support)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandConfigRepository brandConfigRepository;

    /**
     * Get brand by code
     */
    @Cacheable(value = "brands", key = "#brandCode")
    public Optional<Brand> getBrandByCode(String brandCode) {
        log.debug("Getting brand by code: {}", brandCode);
        return brandRepository.findByBrandCode(brandCode);
    }

    /**
     * Get brand by domain
     */
    @Cacheable(value = "brands", key = "#domain")
    public Optional<Brand> getBrandByDomain(String domain) {
        log.debug("Getting brand by domain: {}", domain);
        return brandRepository.findByDomain(domain);
    }

    /**
     * Get default brand (WIN777)
     */
    @Cacheable(value = "brands", key = "'default'")
    public Brand getDefaultBrand() {
        return brandRepository.findByBrandCode("WIN777")
                .orElseGet(() -> {
                    log.warn("Default brand WIN777 not found, creating it");
                    Brand brand = Brand.builder()
                            .brandCode("WIN777")
                            .brandName("WIN777 Platform")
                            .domain("win777.com")
                            .isActive(true)
                            .build();
                    return brandRepository.save(brand);
                });
    }

    /**
     * Create new brand
     */
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public Brand createBrand(String brandCode, String brandName, String domain) {
        log.info("Creating new brand: {}", brandCode);
        
        if (brandRepository.existsByBrandCode(brandCode)) {
            throw new IllegalArgumentException("Brand with code " + brandCode + " already exists");
        }

        Brand brand = Brand.builder()
                .brandCode(brandCode)
                .brandName(brandName)
                .domain(domain)
                .isActive(true)
                .build();

        return brandRepository.save(brand);
    }

    /**
     * Get all active brands
     */
    public List<Brand> getAllActiveBrands() {
        return brandRepository.findAll().stream()
                .filter(Brand::getIsActive)
                .collect(Collectors.toList());
    }

    /**
     * Get brand configuration
     */
    @Cacheable(value = "brandConfigs", key = "#brand.id + ':' + #configKey")
    public Optional<String> getBrandConfig(Brand brand, String configKey) {
        log.debug("Getting brand config: {} for brand: {}", configKey, brand.getBrandCode());
        return brandConfigRepository.findByBrandAndConfigKeyAndIsActiveTrue(brand, configKey)
                .map(BrandConfig::getConfigValue);
    }

    /**
     * Get all brand configurations as map
     */
    @Cacheable(value = "brandConfigs", key = "#brand.id")
    public Map<String, String> getAllBrandConfigs(Brand brand) {
        log.debug("Getting all configs for brand: {}", brand.getBrandCode());
        List<BrandConfig> configs = brandConfigRepository.findByBrandAndIsActiveTrue(brand);
        return configs.stream()
                .collect(Collectors.toMap(
                        BrandConfig::getConfigKey,
                        BrandConfig::getConfigValue
                ));
    }

    /**
     * Set brand configuration
     */
    @Transactional
    @CacheEvict(value = "brandConfigs", allEntries = true)
    public BrandConfig setBrandConfig(Brand brand, String configKey, String configValue, String configType, String description) {
        log.info("Setting brand config: {} = {} for brand: {}", configKey, configValue, brand.getBrandCode());
        
        Optional<BrandConfig> existingConfig = brandConfigRepository
                .findByBrandAndConfigKeyAndIsActiveTrue(brand, configKey);

        if (existingConfig.isPresent()) {
            BrandConfig config = existingConfig.get();
            config.setConfigValue(configValue);
            config.setConfigType(configType);
            config.setDescription(description);
            config.setUpdatedAt(LocalDateTime.now());
            return brandConfigRepository.save(config);
        } else {
            BrandConfig config = BrandConfig.builder()
                    .brand(brand)
                    .configKey(configKey)
                    .configValue(configValue)
                    .configType(configType)
                    .description(description)
                    .isActive(true)
                    .build();
            return brandConfigRepository.save(config);
        }
    }

    /**
     * Delete brand configuration
     */
    @Transactional
    @CacheEvict(value = "brandConfigs", allEntries = true)
    public void deleteBrandConfig(Brand brand, String configKey) {
        log.info("Deleting brand config: {} for brand: {}", configKey, brand.getBrandCode());
        brandConfigRepository.findByBrandAndConfigKeyAndIsActiveTrue(brand, configKey)
                .ifPresent(config -> {
                    config.setIsActive(false);
                    brandConfigRepository.save(config);
                });
    }
}
