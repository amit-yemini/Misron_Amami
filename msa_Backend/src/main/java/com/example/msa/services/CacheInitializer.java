package com.example.msa.services;

import com.example.msa.entities.AlertToMissile;
import com.example.msa.entities.AlertType;
import com.example.msa.entities.LaunchCountry;
import com.example.msa.entities.MissileType;
import com.example.msa.repositories.AlertToMissileRepository;
import com.example.msa.repositories.AlertTypeRepository;
import com.example.msa.repositories.LaunchCountryRepository;
import com.example.msa.repositories.MissileTypeRepository;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheInitializer implements ApplicationRunner {
    @Autowired
    private LaunchCountryRepository launchCountryRepository;
    @Autowired
    private AlertTypeRepository alertTypeRepository;
    @Autowired
    private MissileTypeRepository missileTypeRepository;
    @Autowired
    private AlertToMissileRepository alertToMissileRepository;

    @Autowired
    private Cache<Integer, LaunchCountry> launchCountryCache;
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;
    @Autowired
    private Cache<AlertToMissile, Object> alertToMissileCache;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadLaunchCountries();
        loadAlertTypes();
        loadMissileTypes();
        loadAlertToMissile();
    }

    private void loadLaunchCountries() {
        launchCountryRepository.findAll().forEach(
                launchCountry ->
                        launchCountryCache.put(launchCountry.getId(), launchCountry)
        );
    }

    private void loadAlertTypes() {
        alertTypeRepository.findAll().forEach(
                alertType ->
                        alertTypeCache.put(alertType.getId(), alertType)
        );
    }

    private void loadMissileTypes() {
        missileTypeRepository.findAll().forEach(
                missileType ->
                        missileTypeCache.put(missileType.getId(), missileType)
        );
    }

    private void loadAlertToMissile() {
        alertToMissileRepository.findAll().forEach(
                alertToMissile ->
                        alertToMissileCache.put(alertToMissile, alertToMissile)
        );
    }
}
