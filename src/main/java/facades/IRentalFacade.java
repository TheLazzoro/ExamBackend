package facades;

import dtos.RentalDTO;
import dtos.RentalsDTO;
import dtos.TenantDTO;
import errorhandling.NotFoundException;

public interface IRentalFacade {

    RentalDTO create(RentalDTO rentalDTO);
    RentalsDTO getAll();
    RentalDTO getById(long id) throws NotFoundException;
    RentalsDTO getRentalsByTenant(TenantDTO tenantDTO);
    void delete(RentalDTO rentalDTO) throws NotFoundException;

}
