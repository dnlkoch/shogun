package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.boot.dto.ApplicationInfo;
import de.terrestris.shogun.boot.service.ApplicationInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller that delivers general application information.
 */
@RestController
@RequestMapping("/info")
@Log4j2
public class ApplicationInfoController {

    @Autowired
    private ApplicationInfoService infoService;

    /**
     * Application info endpoint.
     *
     * @return the general application info
     */
    @GetMapping("/app")
    public ApplicationInfo info() {
        try {
            return infoService.getApplicationInfo();
        } catch (Exception e) {
            log.error("Could not determine general application information: {}", e.getMessage());
            log.trace("Full stack trace: ", e);
        }

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
