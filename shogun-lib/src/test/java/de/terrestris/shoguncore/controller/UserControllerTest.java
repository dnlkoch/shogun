package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.User;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
    User.class
})
public class UserControllerTest extends BaseControllerTest<UserController, User> {

    @Before
    public void setBasePath() {
        basePath = "/users";
    }

}
