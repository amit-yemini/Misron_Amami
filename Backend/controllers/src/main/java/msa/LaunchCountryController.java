package msa;

import jakarta.persistence.EntityNotFoundException;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.MissileTypeCacheService;
import msa.DBEntities.AlertType;
import msa.DBEntities.LaunchCountry;
import msa.DBEntities.MissileType;
import msa.mappers.MissileTypeMapper;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class LaunchCountryController {
    @Autowired
    private Cache<Integer, LaunchCountry> launchCountryCache;
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;
    @Autowired
    private MissileTypeRepository missileTypeRepository;
    @Autowired
    private AlertTypeRepository alertTypeRepository;
    @Autowired
    private MissileTypeMapper missileTypeMapper;

    @Autowired
    private MissileTypeCacheService missileTypeCacheService;
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;

    @GetMapping("countries")
    public ResponseEntity<Object> getCountries() {
        return new ResponseEntity<>(launchCountryCache, HttpStatus.OK);
    }

    @GetMapping("alerts")
    public ResponseEntity<Object> getAlerts() {
        return new ResponseEntity<>(alertTypeCache, HttpStatus.OK);
    }

    @GetMapping("missiles")
    public ResponseEntity<Object> getMissiles() {
        return new ResponseEntity<>(missileTypeCache, HttpStatus.OK);
    }

    @DeleteMapping("missile/{missileTypeId}")
    @Transactional
    public ResponseEntity<Object> deleteMissile(@PathVariable int missileTypeId) {
        missileTypeRepository.deleteById(missileTypeId);

        return ResponseEntity.ok("deleted");
    }

    @DeleteMapping("alert/{alertTypeId}")
    @Transactional
    public ResponseEntity<Object> deleteAlert(@PathVariable int alertTypeId) {
        alertTypeRepository.deleteById(alertTypeId);

        return ResponseEntity.ok("deleted");
    }

    @PutMapping("missile")
    @Transactional
    public ResponseEntity<Object> updateMissile(@RequestBody MissileTypeDTO dto) {
        missileTypeRepository.save(missileTypeMapper.convert(dto));

        return ResponseEntity.ok("updated");
    }
}
