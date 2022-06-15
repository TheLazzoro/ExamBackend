package facades;

import dtos.RentalDTO;
import entities.House;
import entities.Rental;
import entities.Tenant;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.HashSet;

public class RentalFacade implements IRentalFacade {

    private static RentalFacade instance;
    private static EntityManagerFactory emf;

    public static RentalFacade getRentalFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RentalFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public RentalDTO create(RentalDTO rentalDTO) {
        EntityManager em = emf.createEntityManager();

        TenantFacade tenantFacade = TenantFacade.getTenantFacade(emf);

        try {
            House house = em.find(House.class, rentalDTO.getHouse().getId());
            Date startDate = rentalDTO.getStartDate();
            Date endDate = rentalDTO.getEndDate();
            float priceAnnual = rentalDTO.getPriceAnnual();
            float deposit = rentalDTO.getDeposit();

            Tenant contactPerson = em.find(Tenant.class, rentalDTO.getContactPerson().getId());
            HashSet<Tenant> tenants = new HashSet<>();
            rentalDTO.getTenants().getTenants().forEach(tenantDTO -> {
                Tenant t = em.find(Tenant.class, tenantDTO.getId());
                tenants.add(t);
            });

            Rental rental = new Rental(house, startDate, endDate, priceAnnual, deposit, contactPerson, tenants);

            em.getTransaction().begin();
            em.persist(rental);
            em.getTransaction().commit();

            return new RentalDTO(em.find(Rental.class, rental.getId()));
        }finally {
            em.close();
        }
    }
}
