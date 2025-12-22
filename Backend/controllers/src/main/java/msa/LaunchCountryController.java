package msa;

import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class LaunchCountryController {
    @Autowired
    private Cache<Integer, LaunchCountry> launchCountryCache;
    @Autowired
    private Cache<Integer, AlertType> alertTypeCache;
    @Autowired
    private Cache<Integer, MissileType> missileTypeCache;

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
        System.out.println(missileTypeCache);
        return new ResponseEntity<>(missileTypeCache, HttpStatus.OK);
    }
}
