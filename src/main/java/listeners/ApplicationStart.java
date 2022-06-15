package listeners;

import entities.Role;
import entities.User;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// This '@WebListener' annotation will run the '@PostConstruct' annotated method below
@WebListener
public class ApplicationStart implements ServletContextListener {

    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();

    //@PostConstruct
    public void init() {
        System.out.println("Role & Admin Initialization...");

        EntityManager em = emf.createEntityManager();
        Role roleUser = em.find(Role.class, "user");
        Role roleAdmin = em.find(Role.class, "admin");
        User admin = em.find(User.class, "admin");

        try {
            if (roleUser == null) {
                em.getTransaction().begin();
                em.persist(new Role("user"));
                em.getTransaction().commit();
            }
            if (roleAdmin == null) {
                em.getTransaction().begin();
                em.persist(new Role("admin"));
                em.getTransaction().commit();

                roleAdmin = em.find(Role.class, "admin");
            }
            if(admin == null) {
                admin = new User("admin", "secret");
                admin.addRole(roleAdmin);
                em.getTransaction().begin();
                em.persist(admin);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }

    }

    /* Alternatively, the class could also inherit from 'ServletContextListener' and
     * implement these two methods that would essentially do the same thing.
     *
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
