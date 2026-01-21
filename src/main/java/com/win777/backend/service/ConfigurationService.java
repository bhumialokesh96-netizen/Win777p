package com.win777.backend.service;

import com.win777.backend.entity.AppConfig;
import com.win777.backend.entity.Banner;
import com.win777.backend.repository.AppConfigRepository;
import com.win777.backend.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ConfigurationService {
    
    @Autowired
    private AppConfigRepository appConfigRepository;
    
    @Autowired
    private BannerRepository bannerRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String CONFIG_CACHE_PREFIX = "config:";
    private static final long CACHE_TTL_HOURS = 24;
    
    /**
     * Get configuration value by key with Redis caching
     */
    public String getConfigValue(String key) {
        String cacheKey = CONFIG_CACHE_PREFIX + key;
        
        // Try to get from cache first
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            return cachedValue.toString();
        }
        
        // Get from database
        Optional<AppConfig> config = appConfigRepository.findByConfigKey(key);
        if (config.isPresent() && config.get().getIsActive()) {
            String value = config.get().getConfigValue();
            // Cache it
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_HOURS, TimeUnit.HOURS);
            return value;
        }
        
        return null;
    }
    
    /**
     * Get configuration value with default
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Set configuration value
     */
    @Transactional
    public AppConfig setConfigValue(String key, String value, String configType, String description) {
        Optional<AppConfig> existing = appConfigRepository.findByConfigKey(key);
        
        AppConfig config;
        if (existing.isPresent()) {
            config = existing.get();
            config.setConfigValue(value);
            config.setConfigType(configType);
            config.setDescription(description);
        } else {
            config = AppConfig.builder()
                .configKey(key)
                .configValue(value)
                .configType(configType)
                .description(description)
                .isActive(true)
                .build();
        }
        
        config = appConfigRepository.save(config);
        
        // Update cache
        String cacheKey = CONFIG_CACHE_PREFIX + key;
        redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_HOURS, TimeUnit.HOURS);
        
        return config;
    }
    
    /**
     * Delete configuration
     */
    @Transactional
    public void deleteConfig(String key) {
        appConfigRepository.findByConfigKey(key).ifPresent(config -> {
            appConfigRepository.delete(config);
            // Remove from cache
            redisTemplate.delete(CONFIG_CACHE_PREFIX + key);
        });
    }
    
    /**
     * Get all active configurations
     */
    public List<AppConfig> getAllActiveConfigs() {
        return appConfigRepository.findByIsActiveTrue();
    }
    
    /**
     * Check if app is in maintenance mode
     */
    public boolean isMaintenanceMode() {
        String value = getConfigValue("maintenance.mode", "false");
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Set maintenance mode
     */
    @Transactional
    public void setMaintenanceMode(boolean enabled) {
        setConfigValue("maintenance.mode", String.valueOf(enabled), "BOOLEAN", 
            "System maintenance mode flag");
    }
    
    /**
     * Get theme color
     */
    public String getThemeColor() {
        return getConfigValue("theme.color", "#007bff");
    }
    
    /**
     * Set theme color
     */
    @Transactional
    public void setThemeColor(String color) {
        setConfigValue("theme.color", color, "STRING", "Application theme color");
    }
    
    /**
     * Get all active banners
     */
    public List<Banner> getActiveBanners() {
        List<Banner> banners = bannerRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        LocalDateTime now = LocalDateTime.now();
        
        // Filter by date range
        return banners.stream()
            .filter(banner -> {
                if (banner.getStartDate() != null && now.isBefore(banner.getStartDate())) {
                    return false;
                }
                if (banner.getEndDate() != null && now.isAfter(banner.getEndDate())) {
                    return false;
                }
                return true;
            })
            .toList();
    }
    
    /**
     * Create or update banner
     */
    @Transactional
    public Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }
    
    /**
     * Delete banner
     */
    @Transactional
    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }
    
    /**
     * Get banner by ID
     */
    public Optional<Banner> getBannerById(Long bannerId) {
        return bannerRepository.findById(bannerId);
    }
    
    /**
     * Clear configuration cache
     */
    public void clearConfigCache() {
        // Clear all config keys from Redis
        redisTemplate.keys(CONFIG_CACHE_PREFIX + "*").forEach(key -> redisTemplate.delete(key));
    }
}
