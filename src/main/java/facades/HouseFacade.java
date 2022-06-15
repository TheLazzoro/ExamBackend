package facades;

import dtos.HouseDTO;
import dtos.HousesDTO;
import entities.House;
import errorhandling.API_Exception;
import errorhandling.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class HouseFacade implements IHouseFacade {

    private static HouseFacade instance;
    private static EntityManagerFactory emf;

    public static HouseFacade getHouseFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HouseFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public HouseDTO create(HouseDTO houseDTO) throws API_Exception {
        EntityManager em = getEntityManager();

        if(houseDTO == null)
            throw new API_Exception("Body should be a JSON object.");

        House house = new House(houseDTO.getAddress(), houseDTO.getCity(), houseDTO.getNumberOfRooms());

        try {
            em.getTransaction().begin();
            em.persist(house);
            em.getTransaction().commit();
            return new HouseDTO(em.find(House.class, house.getId()));
        } finally {
            em.close();
        }
    }

    @Override
    public HousesDTO getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<House> query = em.createQuery("SELECT r FROM House r", House.class);
        List<House> houses = query.getResultList();
        List<HouseDTO> list = new ArrayList<>();
        houses.forEach(house -> list.add(new HouseDTO(house)));
        return new HousesDTO(list);
    }

    @Override
    public HouseDTO getById(long id) throws NotFoundException {
        EntityManager em = getEntityManager();
        House rm = em.find(House.class, id);
        if (rm == null)
            throw new NotFoundException("The House entity with ID: "+id+" was not found");

        return new HouseDTO(rm);
    }

    @Override
    public void delete(House house) {
        EntityManager em = getEntityManager();
        House found = em.find(House.class, house.getId());

        try {
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
