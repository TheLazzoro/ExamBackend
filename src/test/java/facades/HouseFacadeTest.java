package facades;

import dtos.HouseDTO;
import dtos.HousesDTO;
import entities.House;
import errorhandling.API_Exception;
import errorhandling.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HouseFacadeTest {

    private static EntityManagerFactory emf;
    private static HouseFacade facade;
    private static House house1, house2;

    @BeforeAll
    static void setup() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = HouseFacade.getHouseFacade(emf);
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        house1 = new House("Baltorpvej 90", "Ballerup", 2);
        house2 = new House("Lyngbyvej 20", "København", 3);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("Tenant.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.persist(house1);
            em.persist(house2);
            em.getTransaction().commit();

        }finally {
            em.close();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create() throws API_Exception {
        House house = new House("testvej 1", "testby1", 1);
        HouseDTO houseDTO = facade.create(new HouseDTO(house));

        assertEquals(house.getAddress(), houseDTO.getAddress());
        assertEquals(house.getCity(), houseDTO.getCity());
        assertEquals(house.getNumberOfRooms(), houseDTO.getNumberOfRooms());
    }

    @Test
    void getAll() {
        HousesDTO houses = facade.getAll();
        int expected = 2;
        int actual = houses.getHouses().size();

        assertEquals(expected, actual);
    }

    @Test
    void getById() throws NotFoundException {
        HouseDTO houseDTO = facade.getById(house1.getId());

        assertEquals(house1.getAddress(), houseDTO.getAddress());
        assertEquals(house1.getCity(), houseDTO.getCity());
        assertEquals(house1.getNumberOfRooms(), houseDTO.getNumberOfRooms());
    }

    @Test
    void delete() {
        facade.delete(house1);

        Exception ex = assertThrows(NotFoundException.class, () -> {
            facade.getById(house1.getId());
        });
    }
}