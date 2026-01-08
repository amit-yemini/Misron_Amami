package msa;

import msa.CacheServices.IncomingAlertStateMachineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;

    @ExceptionHandler(AlertProcessingException.class)
    public ResponseEntity<String> alertProcessingException(AlertProcessingException e) {
        incomingAlertStateMachineCacheService.handleErrorInStateMachine(e.getAlert());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
