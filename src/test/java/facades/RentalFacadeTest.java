package facades;

import dtos.HouseDTO;
import dtos.RentalDTO;
import dtos.RentalsDTO;
import dtos.TenantDTO;
import entities.*;
import errorhandling.NotFoundException;
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
    private static Rental rental1;
    private static House house1, house2;
    private static User user1, user2, user3;
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

        user1 = new User("testuser", "testpass");
        user1.addRole(userRole);
        user2 = new User("testuser2", "testpass2");
        user2.addRole(userRole);
        user3 = new User("testuser3", "testpass3");
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

        house1 = new House("testaddress", "testcity", 5);
        house2 = new House("testaddress2", "testcity2", 3);

        HashSet<Tenant> tenants = new HashSet<>();
        tenants.add(tenant1);
        tenants.add(tenant2);
        tenants.add(tenant3);
        rental1 = new Rental(house1, new Date(), new Date(), 100000, 30000, tenant1, tenants);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("Tenant.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(tenant1);
            em.persist(tenant2);
            em.persist(tenant3);
            em.persist(house1);
            em.persist(house2);
            em.persist(rental1);
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
        Rental rental = new Rental(house1, new Date(), new Date(), 100000, 30000, tenant1, tenants);

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

    @Test
    void getAll() {
        RentalsDTO rentalsDTO = facade.getAll();
        int expected = 1;
        int actual = rentalsDTO.getRentals().size();

        assertEquals(expected, actual);
    }

    @Test
    void getRentalsByTenant() {
        RentalsDTO rentals = facade.getRentalsByTenant(new TenantDTO(tenant1));
        int expected = 1;
        int actual = rentals.getRentals().size();

        assertEquals(expected, actual);
    }

    @Test
    void getRentalsByUsername() throws NotFoundException {
        RentalsDTO rentals = facade.getRentalsByUsername(user1.getUserName());
        int expected = 1;
        int actual = rentals.getRentals().size();

        assertEquals(expected, actual);
    }

    @Test
    void delete() throws NotFoundException {
        facade.delete(new RentalDTO(rental1));

        Exception ex = assertThrows(NotFoundException.class, () -> {
            facade.getById(rental1.getId());
        });
    }

    @Test
    void edit() throws NotFoundException {
        RentalDTO rentalDTO = new RentalDTO(rental1);
        rentalDTO.setContactPerson(new TenantDTO(tenant2));
        rentalDTO.setDeposit(1337);
        rentalDTO.setPriceAnnual(7331);
        rentalDTO.setHouse(new HouseDTO(house2));
        RentalDTO edited = facade.edit(rentalDTO);

        assertEquals(rentalDTO.getDeposit(), edited.getDeposit());
        assertEquals(rentalDTO.getDeposit(), edited.getDeposit());
        assertEquals(rentalDTO.getHouse().getAddress(), edited.getHouse().getAddress());
        assertEquals(rentalDTO.getTenants().getTenants().size(), edited.getTenants().getTenants().size());
        assertEquals(rentalDTO.getContactPerson().getName(), edited.getContactPerson().getName());
    }
}