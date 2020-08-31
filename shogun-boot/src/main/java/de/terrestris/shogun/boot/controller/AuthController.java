package de.terrestris.shogun.boot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthController {

    @GetMapping("/isSessionValid")
    public ResponseEntity<?> isSessionValid(Authentication authentication) {
        log.debug("Checking if user is logged in.");

        if (authentication != null && authentication.isAuthenticated()) {
            log.debug("User is logged in!");

            return ResponseEntity.ok().build();
        }

        log.debug("User is NOT logged in!");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
