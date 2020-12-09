package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Role;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    Role.class
})
public class RoleControllerTest extends BaseControllerTest<RoleController, Role> {

    @Before
    public void setBasePath() {
        basePath = "/roles";
    }

}
