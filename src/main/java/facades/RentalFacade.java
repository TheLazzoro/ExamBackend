package facades;

import dtos.RentalDTO;
import dtos.RentalsDTO;
import dtos.TenantDTO;
import entities.House;
import entities.Rental;
import entities.Tenant;
import errorhandling.NotFoundException;
import javassist.runtime.Inner;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
        } finally {
            em.close();
        }
    }

    @Override
    public RentalDTO getById(long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        try {
            Rental rental = em.find(Rental.class, id);
            if (rental == null)
                throw new NotFoundException("Rental with ID: " + id + " was not found.");

            return new RentalDTO(rental);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(RentalDTO rentalDTO) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        Rental toDelete = em.find(Rental.class, rentalDTO.getId());

        if(toDelete == null)
            throw new NotFoundException("Rental with ID: " + rentalDTO.getId() + " was not found.");

        try {
            em.getTransaction().begin();
            em.remove(toDelete);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
    }

    @Override
    public RentalsDTO getRentalsByTenant(TenantDTO tenantDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            Long id = tenantDTO.getId();
            TypedQuery<Rental> tq = em.createQuery("select r from Rental r join r.tenants tenants join tenants.rentals tenant where tenants.id = :tenantId", Rental.class);
            tq.setParameter("tenantId", id);
            List<RentalDTO> rentalDTOS = new ArrayList<>();
            tq.getResultList().forEach(rental -> rentalDTOS.add(new RentalDTO(rental)));

            return new RentalsDTO(rentalDTOS);
        } finally {
            em.close();
        }
    }

    public RentalsDTO getRentalsByUsername(String username) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        TenantFacade tenantFacade = TenantFacade.getTenantFacade(emf);
        TenantDTO tenantDTO = tenantFacade.getByUsername(username);

        try {
            return getRentalsByTenant(tenantDTO);
        } finally {
            em.close();
        }
    }
}
