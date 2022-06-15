package facades;

import dtos.TenantDTO;
import dtos.TenantsDTO;

public interface ITenantFacade {

    TenantsDTO getAll();
    TenantDTO getByUsername(String username);

}
