package de.terrestris.shogun.lib.exception.security.permission;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
public final class EntityAccessDeniedException extends ResponseStatusException {

    public EntityAccessDeniedException(Long entityId, Serializable entityType, String message) {
        super(HttpStatus.NOT_FOUND, message);

        log.info("Access to entity of type {} with ID {} is denied", entityId, entityType);
    }

    public EntityAccessDeniedException(Long entityId, Serializable entityType, MessageSource messageSource) {
        this(
            entityId,
            entityType,
            messageSource.getMessage(
                "BaseController.NOT_FOUND",
                null,
                LocaleContextHolder.getLocale()
            )
        );

        log.error("Could not find permission for entity of type {} with ID {}",
            entityType, entityId);
    }
}
