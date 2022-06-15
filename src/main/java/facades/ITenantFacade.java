package facades;

import dtos.TenantDTO;
import dtos.TenantsDTO;
import errorhandling.NotFoundException;

public interface ITenantFacade {

    TenantsDTO getAll();
    TenantDTO getByUsername(String username) throws NotFoundException;

}
