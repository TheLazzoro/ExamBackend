package facades;

import dtos.RentalDTO;
import dtos.RentalsDTO;
import dtos.TenantDTO;

public interface IRentalFacade {

    RentalDTO create(RentalDTO rentalDTO);
    RentalsDTO getRentalsByTenant(TenantDTO tenantDTO);

}
