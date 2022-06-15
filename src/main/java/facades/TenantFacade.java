package facades;

import dtos.TenantDTO;
import dtos.TenantsDTO;
import entities.Tenant;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class TenantFacade implements ITenantFacade {

    private static TenantFacade instance;
    private static EntityManagerFactory emf;

    public static TenantFacade getTenantFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TenantFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public TenantsDTO getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<Tenant> query = em.createQuery("SELECT t FROM Tenant t", Tenant.class);
        List<Tenant> tenants = query.getResultList();
        List<TenantDTO> list = new ArrayList<>();
        tenants.forEach(house -> list.add(new TenantDTO(house)));
        return new TenantsDTO(list);
    }

    @Override
    public TenantDTO getByUsername(String username) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Tenant> tq = em.createQuery("select t from Tenant t where t.user.userName = :username", Tenant.class);
            tq.setParameter("username", username);
            return new TenantDTO(tq.getSingleResult());
        }finally {
            em.close();
        }
    }
}
