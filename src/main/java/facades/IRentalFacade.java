package facades;

import dtos.RentalDTO;
import entities.Rental;

public interface IRentalFacade {

    RentalDTO create(RentalDTO rentalDTO);

}
