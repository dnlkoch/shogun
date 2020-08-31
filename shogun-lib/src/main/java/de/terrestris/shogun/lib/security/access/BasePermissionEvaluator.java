package de.terrestris.shogun.lib.security.access;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.DefaultPermissionEvaluator;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class BasePermissionEvaluator implements PermissionEvaluator {

    private static final String ANONYMOUS_USERNAME = "ANONYMOUS";

    @Autowired
    protected List<BaseEntityPermissionEvaluator<?>> permissionEvaluators;

    @Autowired
    protected DefaultPermissionEvaluator defaultPermissionEvaluator;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject,
                                 Object permissionObject) {
        log.trace("About to evaluate permission for user '{}' targetDomainObject '{}' " +
            "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);

        if ((authentication == null) || (targetDomainObject == null) ||
            !(permissionObject instanceof String) ||
            (targetDomainObject instanceof Optional && ((Optional) targetDomainObject).isEmpty())) {
            log.trace("Restricting access since not all input requirements are met.");
            return false;
        }

        // fetch user from securityUtil
        Optional<User> userOpt = securityContextUtil.getUserFromAuthentication(authentication);
        User user = userOpt.orElse(null);
        String accountName = user != null ? user.getKeycloakId() : ANONYMOUS_USERNAME;

        final BaseEntity persistentObject;
        if (targetDomainObject instanceof Optional) {
            persistentObject = ((Optional<BaseEntity>) targetDomainObject).get();
        } else {
            persistentObject = (BaseEntity) targetDomainObject;
        }

        final Long persistentObjectId = persistentObject.getId();
        final String persistentObjectSimpleName = targetDomainObject.getClass().getSimpleName();
        final PermissionType permission = PermissionType.valueOf((String) permissionObject);

        log.trace("Evaluating whether user '{}' has permission '{}' on entity '{}' with ID {}",
            accountName, permission, targetDomainObject.getClass().getSimpleName(),
            persistentObjectId);

        BaseEntityPermissionEvaluator entityPermissionEvaluator =
            this.getPermissionEvaluatorForClass(persistentObject);

        if (entityPermissionEvaluator != null) {
            return entityPermissionEvaluator.hasPermission(user, persistentObject, permission);
        }

        log.warn("No permission evaluator for class {} could be found. Permission will " +
            "be restricted", persistentObjectSimpleName);

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        // TODO
        log.trace("HUHUHUHUHUHUHUUHUH");
        return false;
    }

//    public boolean hasClassPermission(Authentication authentication, BaseEntity targetDomainObject, Object permissionObject) {
//        LOG.trace("About to evaluate permission for authentication '{}' targetDomainObject '{}' " +
//                "and permissionObject '{}'", authentication, targetDomainObject, permissionObject);
//
//        if ((authentication == null) || (targetDomainObject == null) || !(permissionObject instanceof String)) {
//            LOG.trace("Restricting access since not all input requirements are met.");
//            return false;
//        }
//
//        User user = this.getUserFromAuthentication(authentication);
//
//        String accountName = user != null ? user.getUsername() : ANONYMOUS_USERNAME;
//
//        final PermissionType permission = PermissionType.valueOf((String) permissionObject);
//
//        LOG.trace("Evaluating whether user '{}' has permission '{}' on class '{}", accountName,
//                permission, targetDomainObject.getClass().getSimpleName());
//
//        BaseEntityPermissionEvaluator entityPermissionEvaluator =
//                this.getPermissionEvaluatorForClass(targetDomainObject);
//
//        return entityPermissionEvaluator.hasPermission(user, targetDomainObject.getClass(), permission);
//    }

    /**
     * Returns the {@BaseEntityPermissionEvaluator} for the given {@BaseEntity}.
     *
     * @param persistentObject
     * @return
     */
    private BaseEntityPermissionEvaluator getPermissionEvaluatorForClass(
        BaseEntity persistentObject) {

        BaseEntityPermissionEvaluator entityPermissionEvaluator = permissionEvaluators.stream()
            .filter(permissionEvaluator -> persistentObject.getClass().equals(
                permissionEvaluator.getEntityClassName()))
            .findAny()
            .orElse(defaultPermissionEvaluator);

        return entityPermissionEvaluator;
    }
}
