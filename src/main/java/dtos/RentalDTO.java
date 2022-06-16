package dtos;

import entities.Rental;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RentalDTO {
    private long id;
    private HouseDTO house;
    private Date startDate;
    private Date endDate;
    private float priceAnnual;
    private float deposit;
    private TenantDTO contactPerson;
    private TenantsDTO tenants;

    public RentalDTO(Rental rental) {
        if (rental == null)
            return;

        if (rental.getId() != null)
            this.id = rental.getId();

        this.house = new HouseDTO(rental.getHouse());
        this.startDate = rental.getStartDate();
        this.endDate = rental.getEndDate();
        this.priceAnnual = rental.getPriceAnnual();
        this.deposit = rental.getDeposit();
        this.contactPerson = new TenantDTO(rental.getContactPerson());

        List<TenantDTO> tenantDTOS = new ArrayList<>();
        rental.getTenants().forEach(tenant -> tenantDTOS.add(new TenantDTO(tenant)));
        this.tenants = new TenantsDTO(tenantDTOS);
    }

    public long getId() {
        return id;
    }

    public HouseDTO getHouse() {
        return house;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public float getPriceAnnual() {
        return priceAnnual;
    }

    public float getDeposit() {
        return deposit;
    }

    public TenantDTO getContactPerson() {
        return contactPerson;
    }

    public TenantsDTO getTenants() {
        return tenants;
    }

    public void setHouse(HouseDTO house) {
        this.house = house;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPriceAnnual(float priceAnnual) {
        this.priceAnnual = priceAnnual;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
    }

    public void setContactPerson(TenantDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setTenants(TenantsDTO tenants) {
        this.tenants = tenants;
    }
}
