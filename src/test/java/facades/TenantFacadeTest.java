package facades;

import dtos.HouseDTO;
import dtos.TenantDTO;
import dtos.TenantsDTO;
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

class TenantFacadeTest {

    private static EntityManagerFactory emf;
    private static TenantFacade facade;
    private static Tenant tenant1, tenant2;
    private static House house1;
    private static Rental rental1;

    @BeforeAll
    static void setup() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = TenantFacade.getTenantFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            Role userRole = new Role("user");
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.persist(userRole);
            em.getTransaction().commit();
        }finally {
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

        tenant1 = new Tenant();
        tenant1.setUser(user1);
        tenant2 = new Tenant();
        tenant2.setUser(user2);
        HashSet<Tenant> tenants = new HashSet<>();
        tenants.add(tenant1);
        tenants.add(tenant2);
        house1 = new House("en vej", "en by", 7);
        rental1 = new Rental(house1, new Date(), new Date(), 35000, 20000, tenant2, tenants);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("Tenant.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(tenant1);
            em.persist(tenant2);
            em.persist(house1);
            em.persist(rental1);
            em.getTransaction().commit();

        }finally {
            em.close();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAll() {
        TenantsDTO tenantsDTO = facade.getAll();
        int expected = 2;
        int actual = tenantsDTO.getTenants().size();

        assertEquals(expected, actual);
    }

    @Test
    void getAllByHouse() {
        TenantsDTO tenantsDTO = facade.getAllByHouse(new HouseDTO(house1));
        int expected = 2;
        int actual = tenantsDTO.getTenants().size();

        assertEquals(expected, actual);
    }

    @Test
    void getByUsername() throws NotFoundException {
        TenantDTO tenantDTO = facade.getByUsername(tenant1.getUser().getUserName());

        assertNotNull(tenantDTO);
        assertEquals(tenant1.getName(), tenantDTO.getName());
        assertEquals(tenant1.getPhone(), tenantDTO.getPhone());
        assertEquals(tenant1.getJob(), tenantDTO.getJob());
    }
}