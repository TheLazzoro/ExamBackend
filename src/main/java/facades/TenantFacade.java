package facades;

import dtos.HouseDTO;
import dtos.TenantDTO;
import dtos.TenantsDTO;
import entities.Rental;
import entities.Tenant;
import errorhandling.NotFoundException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.lang.reflect.Type;
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
    public TenantsDTO getAllByHouse(HouseDTO houseDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Tenant> tq = em.createQuery("select distinct t from Tenant t join t.rentals rentals join rentals.tenants tenant where rentals.house.id = :houseId", Tenant.class);
            tq.setParameter("houseId", houseDTO.getId());
            List<Tenant> tenants = tq.getResultList();
            List<TenantDTO> tenantDTOS = new ArrayList<>();
            tenants.forEach(tenant -> tenantDTOS.add(new TenantDTO(tenant)));

            return new TenantsDTO(tenantDTOS);
        } finally {
            em.close();
        }
    }

    @Override
    public TenantDTO getByUsername(String username) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Tenant> tq = em.createQuery("select t from Tenant t where t.user.userName = :username", Tenant.class);
            tq.setParameter("username", username);
            if (tq.getResultList().size() == 0)
                throw new NotFoundException();

            return new TenantDTO(tq.getSingleResult());
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Long ids = 4L;
        TypedQuery<Tenant> tq = em.createQuery("select distinct t from Tenant t join t.rentals rentals join rentals.tenants tenant where rentals.house.id = :id", Tenant.class);
        tq.setParameter("id", ids);

        tq.getResultList().forEach(tenant -> System.out.println(tenant.getName()));
    }
}
