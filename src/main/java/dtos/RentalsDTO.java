package dtos;

import java.util.List;

public class RentalsDTO {
    private List<RentalDTO> rentals;

    public RentalsDTO(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }

    public List<RentalDTO> getRentals() {
        return rentals;
    }
}
