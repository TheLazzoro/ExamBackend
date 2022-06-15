package dtos;

import java.util.List;

public class TenantsDTO {
    private List<TenantDTO> tenants;

    public TenantsDTO(List<TenantDTO> tenantDTOS) {
        this.tenants = tenantDTOS;
    }

    public List<TenantDTO> getTenants() {
        return tenants;
    }
}
