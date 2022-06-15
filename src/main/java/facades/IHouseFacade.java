package facades;

import dtos.HouseDTO;
import dtos.HousesDTO;
import entities.House;
import errorhandling.API_Exception;
import errorhandling.NotFoundException;

public interface IHouseFacade {
    HouseDTO create(HouseDTO houseDTO) throws API_Exception;
    HousesDTO getAll();
    HouseDTO getById(long id) throws NotFoundException;
    void delete(House house);
}
