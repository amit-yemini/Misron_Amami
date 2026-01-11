package msa;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
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

    public void executeFallback(Throwable t) {
        log.error("Circuit breaker activated: {}", t.getMessage());
        if (t instanceof AlertProcessingException e) {
            alertStateMachineService.handleErrorInStateMachine(e.getAlert());
        }
    }
}
