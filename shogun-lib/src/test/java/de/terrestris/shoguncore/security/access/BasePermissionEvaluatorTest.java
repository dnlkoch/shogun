package de.terrestris.shoguncore.security.access;

import de.terrestris.shoguncore.enumeration.PermissionType;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.repository.UserRepository;
import de.terrestris.shoguncore.security.access.entity.ApplicationPermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.BaseEntityPermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.DefaultPermissionEvaluator;
import de.terrestris.shoguncore.specification.UserSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasePermissionEvaluatorTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private DefaultPermissionEvaluator defaultPermissionEvaluatorMock;

    @Mock
    private ApplicationPermissionEvaluator applicationPermissionEvaluatorMock;

    @Spy
    private ArrayList<BaseEntityPermissionEvaluator> baseEntityPermissionEvaluatorMock;

    @InjectMocks
    private BasePermissionEvaluator permissionEvaluator;

    private String mockUserMail = "test@shogun.de";
    private User mockUser;

    @Before
    public void setup() {
        mockUser = new User();
        mockUser.setEmail(mockUserMail);

        when(defaultPermissionEvaluatorMock.getEntityClassName()).thenReturn(BaseEntity.class);
        when(applicationPermissionEvaluatorMock.getEntityClassName()).thenReturn(Application.class);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfAuthenticationIsNull() {
        Authentication authentication = null;
        Application targetDomainObject = new Application();
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfTargetDomainObjectIsNull() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = null;
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfPermissionObjectIsNull() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = new Application();
        String permissionObject = null;

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfPermissionObjectIsNotAString() {
        Authentication authentication = mock(Authentication.class);
        Application targetDomainObject = new Application();
        int permissionObject = 42;

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
        verify(authentication, times(0)).getPrincipal();
        verifyNoMoreInteractions(authentication);
    }

    @Test
    public void hasPermission_ShouldRestrictAccessIfNoUserIsAvailable() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("Not a User object");

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        boolean permissionResult = permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        assertFalse(permissionResult);
        verify(authentication, times(1)).getPrincipal();
        verifyNoMoreInteractions(authentication);
    }

    @Test
    public void hasPermission_ShouldCallTheDefaultPermissionEvaluatorIfNoExplicitImplementationIsAvailable() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserMail);

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);

        when(userRepositoryMock.findOne(any())).thenReturn(Optional.of(mockUser));

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(defaultPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userRepositoryMock);
        baseEntityPermissionEvaluatorMock.clear();
    }

    @Test
    public void hasPermission_ShouldCallTheAppropriatePermissionEvaluatorImplementation() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("Not a User object");

        Application targetDomainObject = new Application();
        targetDomainObject.setId(1L);
        String permissionObject = "READ";

        baseEntityPermissionEvaluatorMock.add(defaultPermissionEvaluatorMock);
        baseEntityPermissionEvaluatorMock.add(applicationPermissionEvaluatorMock);

        when(userRepositoryMock.findOne(any())).thenReturn(Optional.of(mockUser));

        permissionEvaluator.hasPermission(authentication, targetDomainObject, permissionObject);

        verify(applicationPermissionEvaluatorMock, times(1)).hasPermission(mockUser, targetDomainObject, PermissionType.READ);

        reset(userRepositoryMock);
    }

    // TODO Fix test
//    @Test
//    public void hasPermission_ShouldRestrictAccessForSecuredTargetDomainObjectWithoutPermissions() throws NoSuchFieldException, IllegalAccessException {
//        Authentication authenticationMock = mock(Authentication.class);
//        final User user = new User();
//        user.setUsername("Test User");
//        user.setId(1909L);
//
//        when(authenticationMock.getPrincipal()).thenReturn(user);
//
//        final Long userId = user.getId();
//
//        Application targetDomainObject = new Application();
//        targetDomainObject.setId(1L);
//        final Class<?> domainObjectClass = targetDomainObject.getClass();
//
//        String permissionObject = "READ";
//        final PermissionType permission = PermissionType.valueOf(permissionObject);
//
//        final boolean expectedPermission = false;
//
//        BaseEntityPermissionEvaluator baseEntityPermissionEvaluatorMock = mock(BaseEntityPermissionEvaluator.class);
//        when(baseEntityPermissionEvaluatorMock.hasPermission(user, targetDomainObject, PermissionType.valueOf(permissionObject))).thenReturn(expectedPermission);
//
//        when(permissionEvaluatorFactoryMock.getEntityPermissionEvaluator(domainObjectClass)).thenReturn(baseEntityPermissionEvaluatorMock);
//
//        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
//
//        boolean permissionResult = permissionEvaluator.hasPermission(authenticationMock, targetDomainObject, permissionObject);
//
//        assertEquals(expectedPermission, permissionResult);
//        verify(baseEntityPermissionEvaluatorMock, times(1)).hasPermission(user, targetDomainObject, permission);
//        verifyNoMoreInteractions(baseEntityPermissionEvaluatorMock);
//
//        verify(permissionEvaluatorFactoryMock, times(1)).getEntityPermissionEvaluator(domainObjectClass);
//        verifyNoMoreInteractions(permissionEvaluatorFactoryMock);
//
//        verify(userRepositoryMock, times(1)).findById(userId);
//        verifyNoMoreInteractions(userRepositoryMock);
//
//        verify(authenticationMock, times(1)).getPrincipal();
//        verifyNoMoreInteractions(authenticationMock);
//    }

    // TODO Fix test
//    @Test
//    public void hasPermission_ShouldRestrictAccessForSecuredTargetDomainObjectWithPermissions() throws NoSuchFieldException, IllegalAccessException {
//        Authentication authenticationMock = mock(Authentication.class);
//        final User user = new User();
//        user.setUsername("Test User");
//        user.setId(1909L);
//
//        when(authenticationMock.getPrincipal()).thenReturn(user);
//
//        final Long userId = user.getId();
//
//        Application targetDomainObject = new Application();
//        targetDomainObject.setId(1L);
//        final Class<?> domainObjectClass = targetDomainObject.getClass();
//
//        String permissionObject = "READ";
//        final PermissionType permission = PermissionType.valueOf(permissionObject);
//
//        final boolean expectedPermission = true;
//
//        BaseEntityPermissionEvaluator baseEntityPermissionEvaluatorMock = mock(BaseEntityPermissionEvaluator.class);
//        when(baseEntityPermissionEvaluatorMock.hasPermission(user, targetDomainObject, PermissionType.valueOf(permissionObject))).thenReturn(expectedPermission);
//
//        when(permissionEvaluatorFactoryMock.getEntityPermissionEvaluator(domainObjectClass)).thenReturn(baseEntityPermissionEvaluatorMock);
//
//        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
//
//        boolean permissionResult = permissionEvaluator.hasPermission(authenticationMock, targetDomainObject, permissionObject);
//
//        assertEquals(expectedPermission, permissionResult);
//
//        verify(baseEntityPermissionEvaluatorMock, times(1)).hasPermission(user, targetDomainObject, permission);
//        verifyNoMoreInteractions(baseEntityPermissionEvaluatorMock);
//
//        verify(permissionEvaluatorFactoryMock, times(1)).getEntityPermissionEvaluator(domainObjectClass);
//        verifyNoMoreInteractions(permissionEvaluatorFactoryMock);
//
//        verify(userRepositoryMock, times(1)).findById(userId);
//        verifyNoMoreInteractions(userRepositoryMock);
//
//        verify(authenticationMock, times(1)).getPrincipal();
//        verifyNoMoreInteractions(authenticationMock);
//    }

}
