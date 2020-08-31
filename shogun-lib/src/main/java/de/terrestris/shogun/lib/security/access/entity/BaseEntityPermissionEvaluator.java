package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.permission.PermissionCollection;
import de.terrestris.shogun.lib.service.security.permission.GroupClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.GroupInstancePermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserClassPermissionService;
import de.terrestris.shogun.lib.service.security.permission.UserInstancePermissionService;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

@Log4j2
public abstract class BaseEntityPermissionEvaluator<E extends BaseEntity>
    implements EntityPermissionEvaluator<E> {

    @Autowired
    protected UserInstancePermissionService userInstancePermissionService;

    @Autowired
    protected GroupInstancePermissionService groupInstancePermissionService;

    @Autowired
    protected UserClassPermissionService userClassPermissionService;

    @Autowired
    protected GroupClassPermissionService groupClassPermissionService;

    @Override
    public Class<E> getEntityClassName() {
        return (Class<E>) GenericTypeResolver
            .resolveTypeArgument(getClass(), BaseEntityPermissionEvaluator.class);
    }

    @Override
    public boolean hasPermission(User user, E entity, PermissionType permission) {
        final String simpleClassName = entity.getClass().getSimpleName();

        // CHECK USER INSTANCE PERMISSIONS
        PermissionCollection userPermissionCol;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            userPermissionCol = new PermissionCollection();
        } else {
            userPermissionCol =
                userInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> userInstancePermissions = userPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the user has the ADMIN permission
        if (userInstancePermissions.contains(permission) ||
            userInstancePermissions.contains(PermissionType.ADMIN)) {
            log.trace("Granting " + permission + " access by user instance permissions");
            return true;
        }

        // CHECK GROUP INSTANCE PERMISSIONS
        PermissionCollection groupPermissionsCol = null;
        if (permission.equals(PermissionType.CREATE) && entity.getId() == null) {
            groupPermissionsCol = new PermissionCollection();
        } else {
            groupPermissionsCol =
                groupInstancePermissionService.findPermissionCollectionFor(entity, user);
        }
        final Set<PermissionType> groupInstancePermissions = groupPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (groupInstancePermissions.contains(permission) ||
            groupInstancePermissions.contains(PermissionType.ADMIN)) {
            log.trace("Granting " + permission + " access by group instance permissions");
            return true;
        }

        // CHECK USER CLASS PERMISSIONS
        PermissionCollection userClassPermissionCol =
            userClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> userClassPermissions = userClassPermissionCol.getPermissions();

        // Grant access if user explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (userClassPermissions.contains(permission) ||
            userClassPermissions.contains(PermissionType.ADMIN)) {
            log.trace("Granting " + permission + " access by user class permissions");
            return true;
        }

        // CHECK GROUP CLASS PERMISSIONS
        PermissionCollection groupClassPermissionsCol =
            groupClassPermissionService.findPermissionCollectionFor(entity, user);
        final Set<PermissionType> groupClassPermissions = groupClassPermissionsCol.getPermissions();

        // Grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (groupClassPermissions.contains(permission) ||
            groupClassPermissions.contains(PermissionType.ADMIN)) {
            log.trace("Granting " + permission + " access by group instance permissions");
            return true;
        }

        log.trace("Restricting " + permission + " access on secured object '" +
            simpleClassName + "' with ID " + entity.getId());

        return false;
    }
}
