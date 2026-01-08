package msa;

import msa.CacheServices.IncomingAlertStateMachineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services/alerts")
public class AlertController {
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;

    @PostMapping("/in")
    public ResponseEntity<Object> newAlert(@RequestBody Alert alert) {
        incomingAlertStateMachineCacheService.addIncomingAlert(alert);

        return new ResponseEntity<>("Alert in", HttpStatus.OK);
    }
}
