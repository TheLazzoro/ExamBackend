package entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;

@Entity
@NamedQuery(name = "Rental.deleteAllRows", query = "DELETE from Rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Date startDate;
    private Date endDate;
    private float priceAnnual;
    private float deposit;

    @ManyToOne
    private House house;

    @OneToOne
    private Tenant contactPerson;

    @ManyToMany
    @JoinTable(
            name = "RENTAL_TENANT",
            joinColumns = @JoinColumn(name = "rental_id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id")
    )
    HashSet<Tenant> tenants = new HashSet<>();

    public Rental() {
    }

    public Rental(House house, Date startDate, Date endDate, float priceAnnual, float deposit, Tenant contactPerson, HashSet<Tenant> tenants) {
        this.house = house;
        this.startDate = startDate;
        this.endDate = endDate;
        this. priceAnnual =  priceAnnual;
        this.deposit = deposit;
        this.contactPerson = contactPerson;
        this.tenants = tenants;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public void addTenant(Tenant tenant) {
        tenants.add(tenant);
    }

    public Tenant getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Tenant contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public House getHouse() {
        return house;
    }

    public HashSet<Tenant> getTenants() {
        return tenants;
    }
}
