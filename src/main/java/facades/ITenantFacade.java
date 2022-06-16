package facades;

import dtos.HouseDTO;
import dtos.TenantDTO;
import dtos.TenantsDTO;
import errorhandling.NotFoundException;

public interface ITenantFacade {

    TenantsDTO getAll();
    TenantsDTO getAllByHouse(HouseDTO houseDTO);
    TenantDTO getByUsername(String username) throws NotFoundException;

}
