package dev.payforge.identity;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    record PingResponse(String service, String message, Instant time) {}

    @GetMapping("/v1/ping")
    PingResponse ping() {
        return new PingResponse("identity-service", "pong", Instant.now());
    }
}