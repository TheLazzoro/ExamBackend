package dtos;

public class UserDTO {
    private String username;
    private String password;
    private TenantDTO tenant;

    public UserDTO(String username, String password, TenantDTO tenantDTO) {
        this.username = username;
        this.password = password;
        this.tenant = tenantDTO;
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public TenantDTO getTenant() {
        return tenant;
    }
}
