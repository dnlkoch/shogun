package de.terrestris.shogun.lib.security;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.repository.GroupRepository;
import de.terrestris.shogun.lib.repository.UserRepository;
import de.terrestris.shogun.lib.util.KeycloakUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SecurityContextUtil {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    protected RealmResource keycloakRealm;

    @Autowired
    KeycloakUtil keycloakUtil;

    /**
     * Return keycloak user id from {@link Authentication} object
     * - from {@link IDToken}
     * - from {@link org.keycloak.Token}
     *
     * @param authentication The Spring security authentication
     * @return The keycloak user id token
     */
    public static String getKeycloakUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) authentication.getPrincipal();
            KeycloakSecurityContext keycloakSecurityContext =
                keycloakPrincipal.getKeycloakSecurityContext();
            IDToken idToken = keycloakSecurityContext.getIdToken();
            String keycloakUserId;

            if (idToken != null) {
                keycloakUserId = idToken.getSubject();
            } else {
                AccessToken accessToken = keycloakSecurityContext.getToken();
                keycloakUserId = accessToken.getSubject();
            }

            return keycloakUserId;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserBySession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakUserId =
            SecurityContextUtil.getKeycloakUserIdFromAuthentication(authentication);

        if (StringUtils.isEmpty(keycloakUserId)) {
            return Optional.empty();
        }

        Optional<User> user = userRepository.findByKeycloakId(keycloakUserId);

        if (user.isPresent()) {
            UserResource userResource = keycloakUtil.getUserResource(user.get());
            UserRepresentation userRepresentation = userResource.toRepresentation();
            user.get().setKeycloakRepresentation(userRepresentation);
        }

        return user;
    }

    /**
     * @return
     */
    public List<GrantedAuthority> getGrantedAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ArrayList<>(authentication.getAuthorities());
    }

    /**
     * Returns the current user object from the database.
     *
     * @param authentication
     * @return
     */
    public Optional<User> getUserFromAuthentication(Authentication authentication) {
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof KeycloakPrincipal)) {
            return Optional.empty();
        }
        // get user info from authentication object
        String keycloakUserId = getKeycloakUserIdFromAuthentication(authentication);
        return userRepository.findByKeycloakId(keycloakUserId);
    }

    /**
     * Get (SHOGun) groups for user based on actual assignment in keycloak
     *
     * @param user The SHOGun user
     * @return List of groups
     */
    public List<Group> getGroupsForUser(User user) {
        List<GroupRepresentation> userGroups = this.getKeycloakGroupsForUser(user);
        if (userGroups == null) {
            return null;
        }

        // return list of Groups that are in SHOGun DB
        return userGroups.stream()
            .map(GroupRepresentation::getId)
            .map(keycloakGroupId -> groupRepository.findByKeycloakId(keycloakGroupId).get())
            .collect(Collectors.toList());
    }

    /**
     * Return keycloak GroupRepresentaions (groups) for user
     *
     * @param user
     * @return
     */
    public List<GroupRepresentation> getKeycloakGroupsForUser(User user) {
        UsersResource users = this.keycloakRealm.users();
        UserResource kcUser = users.get(user.getKeycloakId());
        return kcUser.groups();
    }

    /**
     * Fetch user name of user from keycloak
     *
     * @param user
     * @return
     */
    public String getUserNameFromKeycloak(User user) {
        UsersResource users = this.keycloakRealm.users();
        UserResource kcUser = users.get(user.getKeycloakId());
        UserRepresentation kcUserRepresentation = kcUser.toRepresentation();
        return String.format("%s %s", kcUserRepresentation.getFirstName(),
            kcUserRepresentation.getLastName());
    }
}
