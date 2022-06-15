package facades;

import dtos.RentalDTO;
import entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class RentalFacadeTest {

    private static EntityManagerFactory emf;
    private static RentalFacade facade;
    private static Rental rental;
    private static House house;
    private static Tenant tenant1, tenant2, tenant3;

    @BeforeAll
    static void setup() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = RentalFacade.getRentalFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            Role userRole = new Role("user");
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.persist(userRole);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        Role userRole = em.find(Role.class, "user");

        User user1 = new User("testuser", "testpass");
        user1.addRole(userRole);
        User user2 = new User("testuser2", "testpass2");
        user2.addRole(userRole);
        User user3 = new User("testuser3", "testpass3");
        user3.addRole(userRole);

        tenant1 = new Tenant();
        tenant1.setUser(user1);
        tenant1.setName("Lasse");
        tenant2 = new Tenant();
        tenant2.setUser(user2);
        tenant2.setName("Christian");
        tenant3 = new Tenant();
        tenant3.setUser(user3);
        tenant3.setName("Nikolaj");


        house = new House("testaddress", "testcity", 5);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("Tenant.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(tenant1);
            em.persist(tenant2);
            em.persist(tenant3);
            em.persist(house);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create() {
        HashSet<Tenant> tenants = new HashSet<>();
        tenants.add(tenant1);
        tenants.add(tenant2);
        tenants.add(tenant3);
        rental = new Rental(house, new Date(), new Date(), 100000, 30000, tenant1, tenants);

        RentalDTO rentalDTO = new RentalDTO(rental);
        RentalDTO returned = facade.create(rentalDTO);

        assertEquals(rentalDTO.getHouse().getAddress(), returned.getHouse().getAddress());
        assertEquals(rentalDTO.getHouse().getCity(), returned.getHouse().getCity());
        assertEquals(rentalDTO.getHouse().getNumberOfRooms(), returned.getHouse().getNumberOfRooms());

        assertEquals(rentalDTO.getContactPerson().getName(), returned.getContactPerson().getName());
        assertEquals(rentalDTO.getTenants().getTenants().size(), returned.getTenants().getTenants().size());

        assertEquals(rentalDTO.getDeposit(), returned.getDeposit());
        assertEquals(rentalDTO.getPriceAnnual(), returned.getPriceAnnual());
    }
}