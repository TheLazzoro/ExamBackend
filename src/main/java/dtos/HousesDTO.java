package dtos;

import java.util.List;

public class HousesDTO {
    private List<HouseDTO> houses;

    public HousesDTO(List<HouseDTO> houses) {
        this.houses = houses;
    }

    public List<HouseDTO> getHouses() {
        return houses;
    }
}
