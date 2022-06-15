package dtos;

import entities.House;
import entities.Tenant;

import java.util.List;

public class TenantDTO {
    private long id;
    private String name;
    private String phone;
    private String job;

    public TenantDTO(Tenant tenant) {
        if(tenant == null)
            return;

        this.name = tenant.getName();
        this.phone = tenant.getPhone();
        this.job = tenant.getJob();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getJob() {
        return job;
    }
}
