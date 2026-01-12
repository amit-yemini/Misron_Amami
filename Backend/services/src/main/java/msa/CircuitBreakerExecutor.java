package msa;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CircuitBreakerExecutor {
    @Autowired
    private AlertStateMachineService alertStateMachineService;

    @CircuitBreaker(name = "alertProcessing", fallbackMethod = "executeFallback")
    public void execute(Runnable action) {
        action.run();
    }

    public void executeFallback(Throwable throwable) {
        log.error("Circuit breaker activated: {}", throwable.getMessage());
        if (throwable instanceof AlertProcessingException exception) {
            alertStateMachineService.handleErrorInStateMachine(exception.getAlert());
        }
    }
}
