package de.terrestris.shogun.lib.util;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class KeycloakUtil {

    @Autowired
    protected RealmResource keycloakRealm;

    public UserResource getUserResource(User user) {
        UsersResource kcUsers = this.keycloakRealm.users();
        return kcUsers.get(user.getKeycloakId());
    }

    public GroupResource getGroupResource(Group group) {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.group(group.getKeycloakId());
    }

    public GroupResource getGroupResource(String id) {
        GroupsResource kcGroups = this.keycloakRealm.groups();
        return kcGroups.group(id);
    }

    public void addUserToGroup(User user, Group group) {
        UserResource kcUser = this.getUserResource(user);
        GroupResource kcGroup = this.getGroupResource(group);

        kcUser.joinGroup(kcGroup.toRepresentation().getId());
    }

    public void addUserToGroup(User user, GroupRepresentation kcGroup) {
        UserResource kcUser = this.getUserResource(user);

        kcUser.joinGroup(kcGroup.getId());
    }

    public GroupResource getResourceFromRepresentation(GroupRepresentation representation) {
        return this.keycloakRealm.groups().group(representation.getId());
    }

    public UserResource getResourceFromRepresentation(UserRepresentation representation) {
        return this.keycloakRealm.users().get(representation.getId());
    }

    public boolean addSubGroupToGroup(GroupRepresentation parentGroup,
                                      GroupRepresentation subGroup) {
        String subGroupName = subGroup.getName();
        String groupName = parentGroup.getName();
        GroupResource parentGroupResource = this.getResourceFromRepresentation(parentGroup);
        try (Response response = parentGroupResource.subGroup(subGroup)) {
            if (response.getStatusInfo().equals(Response.Status.NO_CONTENT)) {
                log.info("Added group " + subGroupName + " as SubGroup to " + groupName);
                return true;
            } else {
                String message =
                    "Error adding group " + subGroupName + " as SubGroup to " + groupName +
                        ", Error Message : " + response;
                log.error(message);
                throw new WebApplicationException(message, response);
            }
        }
    }

    public List<GroupRepresentation> getGroupByName(String groupName) {
        GroupsResource kcGroupRepresentation = this.keycloakRealm.groups();
        return kcGroupRepresentation.groups().stream()
            .filter(groupRepresentation -> StringUtils
                .equalsIgnoreCase(groupName, groupRepresentation.getName()))
            .collect(Collectors.toList());
    }

    public Boolean existsGroup(String groupName) {
        List<GroupRepresentation> availableGroups = this.getGroupByName(groupName);

        return !availableGroups.isEmpty();
    }

    public GroupRepresentation addGroup(String groupName) throws WebApplicationException {

        List<GroupRepresentation> availableGroups = this.getGroupByName(groupName);

        if (!availableGroups.isEmpty()) {
            log.debug("Group {} already exists.", groupName);

            return availableGroups.get(0);
        }

        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);

        try (Response response = this.keycloakRealm.groups().add(group)) {
            if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
                Response.StatusType statusInfo = response.getStatusInfo();
                response.bufferEntity();
                String body = response.readEntity(String.class);
                String message = "Create method returned status " +
                    statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() +
                    "); expected status: Created (201). Response body: " + body;
                log.error(message);

                throw new WebApplicationException(message, response);
            }

            group.setId(CreatedResponseUtil.getCreatedId(response));

            return group;
        }
    }

    public RolesResource getRoles() {
        return keycloakRealm.roles();
    }

    public boolean isUserInGroup(User user, Group group) {
        UserResource kcUser = this.getUserResource(user);
        GroupResource kcGroup = this.getGroupResource(group);
        return kcUser.groups().contains(kcGroup);
    }

    /**
     * Return keycloak user id from {@link Authentication} object
     * - from {@link IDToken}
     * - from {@link org.keycloak.Token}
     *
     * @param authentication The Spring security authentication
     * @return The keycloak user id token
     */
    public String getKeycloakUserIdFromAuthentication(Authentication authentication) {
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

    public List<GroupRepresentation> getUserGroups(User user) {
        UserResource userResource = this.getUserResource(user);
        List<GroupRepresentation> groups = new ArrayList<>();

        try {
            groups = userResource.groups();
        } catch (Exception e) {
            log.warn(
                "Could not get the GroupRepresentations for the groups of user with SHOGun ID {} and " +
                    "Keycloak ID {}. This may happen if the user is not available in Keycloak.",
                user.getId(), user.getKeycloakId());
            log.trace("Full stack trace: ", e);
        }

        return groups;
    }

}
