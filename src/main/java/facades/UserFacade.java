package facades;

import dtos.UserDTO;
import entities.Role;
import entities.Tenant;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import errorhandling.UserAlreadyExistsException;
import security.errorhandling.AuthenticationException;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public void createUser(UserDTO userDTO) throws UserAlreadyExistsException {
        EntityManager em = emf.createEntityManager();

        User alreadyExists = em.find(User.class, userDTO.getUsername());
        if (alreadyExists != null)
            throw new UserAlreadyExistsException("Username '" + userDTO.getUsername() + "' already exists.");

        try {

            User user = new User(userDTO.getUsername(), userDTO.getPassword());
            Role userRole = em.find(Role.class, "user");
            user.addRole(userRole);
            Tenant tenant = new Tenant(userDTO.getTenant());
            tenant.setUser(user);

            em.getTransaction().begin();
            em.persist(tenant);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

}
