package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.config.JacksonConfig;
import de.terrestris.shoguncore.config.JdbcConfiguration;
import de.terrestris.shoguncore.config.WebConfig;
import de.terrestris.shoguncore.model.BaseEntity;
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
public abstract class BaseControllerTest<U extends BaseController, S extends BaseEntity> implements IBaseController { // <T, S>, T extends BaseService<?, S>, S extends BaseEntity

    @Autowired
    protected S entity;

    @Autowired
    protected U controller;

    protected MockMvc mockMvc;

    protected String basePath;

    @Before
    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void findsAllEntities() throws Exception {
        BaseEntity mockEntity1 = mock(entity.getClass());
        BaseEntity mockEntity2 = mock(entity.getClass());
        BaseEntity mockEntity3 = mock(entity.getClass());

        ArrayList<BaseEntity> entityList = new ArrayList<>();

        entityList.add(mockEntity1);
        entityList.add(mockEntity2);
        entityList.add(mockEntity3);

        this.mockMvc.perform(MockMvcRequestBuilders
            .get(basePath))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string("ADMIN"));
    }
}
