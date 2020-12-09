package de.terrestris.shoguncore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseServiceTest {

    @Mock
    private BaseCrudRepository baseCrudRepositoryMock;

    @Mock
    ObjectMapper objectMapperMock;

    @InjectMocks
    private ApplicationService baseServiceMock;
//    private BaseService baseServiceMock;

    @Before
    public void init() {
//        baseServiceMock = mock(BaseService.class, CALLS_REAL_METHODS);

        //        PowerMockito

//        ReflectionTestUtils.setField(baseServiceMock, "repository", baseCrudRepositoryMock);
//        ReflectionTestUtils.setField(baseServiceMock, "objectMapper", objectMapperMock);
    }

    @Test
    public void test_AbstractClasses() {
        BaseEntity mockEntity1 = mock(BaseEntity.class);
        BaseEntity mockEntity2 = mock(BaseEntity.class);
        BaseEntity mockEntity3 = mock(BaseEntity.class);

        ArrayList<BaseEntity> entityList = new ArrayList<>();

        entityList.add(mockEntity1);
        entityList.add(mockEntity2);
        entityList.add(mockEntity3);

        when(baseCrudRepositoryMock.findAll()).thenReturn(entityList);

        List returnValue = baseServiceMock.findAll();

        assertEquals(returnValue, entityList);

//        when(ac.sayMock()).thenCallRealMethod();
//        when(ac.getName()).thenReturn("Jyotika");
//        assertEquals("Hii.. Jyotika!!", ac.sayMock());
    }
}
