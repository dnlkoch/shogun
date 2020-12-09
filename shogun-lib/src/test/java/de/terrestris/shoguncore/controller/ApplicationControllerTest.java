package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.config.JacksonConfig;
import de.terrestris.shoguncore.config.JdbcConfiguration;
import de.terrestris.shoguncore.config.WebConfig;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.service.*;
import de.terrestris.shoguncore.service.security.IdentityService;
import de.terrestris.shoguncore.service.security.permission.UserInstancePermissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
//    TestContext.class,
    JacksonConfig.class,
    JdbcConfiguration.class,
    WebConfig.class,
    ApplicationService.class,
    UserInstancePermissionService.class,
    SecurityContextUtil.class,
    IdentityService.class,
    FileService.class,
    GroupService.class,
    ImageFileService.class,
    LayerService.class,
    RoleService.class,
    UserService.class,
    BCryptPasswordEncoder.class
})
@WebAppConfiguration
public class ApplicationControllerTest {

//    @Mock
//    private ApplicationRepository applicationRepositoryMock;

    @Autowired
    private ApplicationController applicationController;

//    @InjectMocks
//    @Autowired
//    private ApplicationService applicationServiceMock;

    private MockMvc mockMvc;

//    public ApplicationControllerTest() {
//        applicationServiceMock = Mockito.mock(ApplicationService.class);
//        applicationController = new ApplicationController();
//    }

    @Before
    public void initMocks() {
//        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(applicationController)
            .build();
    }

//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }

    @Test
    public void givenEmployeeNameJohnWhenInvokeRoleThenReturnAdmin() throws Exception {
        Application mockEntity1 = mock(Application.class);
        Application mockEntity2 = mock(Application.class);
        Application mockEntity3 = mock(Application.class);

        ArrayList<Application> entityList = new ArrayList<>();

        entityList.add(mockEntity1);
        entityList.add(mockEntity2);
        entityList.add(mockEntity3);

//        when(applicationServiceMock.findAll()).thenReturn(entityList);

//        List<Application> response = applicationController.findAll();

        this.mockMvc.perform(MockMvcRequestBuilders
            .get("/applications"))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string("ADMIN"));
    }

//    @Test
//    public void test_AbstractClasses() {
//        Application mockEntity1 = mock(Application.class);
//        Application mockEntity2 = mock(Application.class);
//        Application mockEntity3 = mock(Application.class);
//
//        ArrayList<Application> entityList = new ArrayList<>();
//
//        entityList.add(mockEntity1);
//        entityList.add(mockEntity2);
//        entityList.add(mockEntity3);
//
//        when(applicationService.findAll()).thenReturn(entityList);
//
//        List returnValue = applicationService.findAll();
//
//        assertEquals(returnValue, entityList);
//
////        when(ac.sayMock()).thenCallRealMethod();
////        when(ac.getName()).thenReturn("Jyotika");
////        assertEquals("Hii.. Jyotika!!", ac.sayMock());
//    }
}
