package entities;

import dtos.TenantDTO;
import dtos.UserDTO;

import javax.persistence.*;
import java.util.HashSet;

@Entity
@NamedQuery(name = "Tenant.deleteAllRows", query = "DELETE from Tenant")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String phone;
    private String job;

    @OneToOne(cascade = CascadeType.PERSIST)
    private User user;

    @ManyToMany(mappedBy = "tenants")
    HashSet<Rental> rentals = new HashSet<>();

    public Tenant() {
    }

    public Tenant(String name, String phone, String job) {
        this.name = name;
        this.phone = phone;
        this.job = job;
    }

    public Tenant(TenantDTO tenantDTO) {
        this.name = tenantDTO.getName();
        this.phone = tenantDTO.getPhone();
        this.job = tenantDTO.getJob();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
