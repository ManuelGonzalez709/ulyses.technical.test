package com.septeo.ulyses.technical.test.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.septeo.ulyses.technical.test.entity.Brand;
import com.septeo.ulyses.technical.test.service.BrandService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class BrandCache {
    private static final long EXPIRATION_TIME_MS = 5 * 60 * 1000; // 5 minutos

    private final ConcurrentHashMap<Long, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Object listLock = new Object();
    private final Object idLock = new Object();

    @Autowired
    private BrandService brandService;

    // For all brands (list)
    private static class ListCacheEntry {
        List<Brand> brands;
        long timestamp;
        ListCacheEntry(List<Brand> brands, long timestamp) {
            this.brands = brands;
            this.timestamp = timestamp;
        }
    }
    // For single brand for id
    private static class CacheEntry {
        Brand brand;
        long timestamp;
        CacheEntry(Brand brand, long timestamp) {
            this.brand = brand;
            this.timestamp = timestamp;
        }
    }

    private ListCacheEntry allBrandsCache = null;

    /***
     * Method used to obtain brands by id in cache
     * @param id id of thge brand
     * @return returns the brand
     */
    public Optional<Brand> getBrandById(Long id) {
        long now = System.currentTimeMillis();
        CacheEntry entry = cache.get(id);

        if (entry != null && (now - entry.timestamp < EXPIRATION_TIME_MS)) {
            return Optional.ofNullable(entry.brand);
        }

        // Sincronizamos solo la sección crítica por id
        synchronized (idLock) {
            entry = cache.get(id);
            if (entry != null && (now - entry.timestamp < EXPIRATION_TIME_MS)) {
                return Optional.ofNullable(entry.brand);
            }
            Optional<Brand> brandOpt = brandService.getBrandById(id);
            Brand brand = brandOpt.orElse(null);
            cache.put(id, new CacheEntry(brand, now));
            return brandOpt;
        }
    }

    /***
     * Method used to obtain all brands in chache
     * @return returns the list of all the brands
     */
    public List<Brand> getAllBrands() {
        long now = System.currentTimeMillis();
        ListCacheEntry entry = allBrandsCache;

        if (entry != null && (now - entry.timestamp < EXPIRATION_TIME_MS)) {
            return entry.brands;
        }

        // Sincronizamos solo la sección crítica de la lista
        synchronized (listLock) {
            entry = allBrandsCache;
            if (entry != null && (now - entry.timestamp < EXPIRATION_TIME_MS)) {
                return entry.brands;
            }
            List<Brand> brands = brandService.getAllBrands();
            allBrandsCache = new ListCacheEntry(brands, now);
            return brands;
        }
    }
}