package com.lib.alga_foods_security_oauth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/api/session")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void session() {
    }
}
