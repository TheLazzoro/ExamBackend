package dtos;

import entities.House;

import java.util.List;

public class HouseDTO {
    private String address;
    private String city;
    private int numberOfRooms;
    private List<RentalDTO> rentals;

    public HouseDTO(House house) {
        this.address = house.getAddress();
        this.city = house.getCity();
        this.numberOfRooms = house.getNumberOfRooms();
    }

    public HouseDTO(String address, String city, int numberOfRooms) {
        this.address = address;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public List<RentalDTO> getRentals() {
        return rentals;
    }
}
