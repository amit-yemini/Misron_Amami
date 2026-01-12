package msa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services/alerts")
public class AlertController {
    @Autowired
    private AlertStateMachineService alertStateMachineService;

    @PostMapping("/in")
    public ResponseEntity<Object> newAlert(@RequestBody Alert alert) {
        alertStateMachineService.addIncomingAlert(alert);

        return new ResponseEntity<>("Alert in", HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{incidentId}")
    public ResponseEntity<Object> cancelAlert(@PathVariable int incidentId) {
        alertStateMachineService.cancelAlert(incidentId);

        return new ResponseEntity<>("Alert cancelled", HttpStatus.OK);
    }
}
