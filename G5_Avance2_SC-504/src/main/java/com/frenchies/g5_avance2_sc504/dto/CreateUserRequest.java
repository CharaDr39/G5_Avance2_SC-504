package com.frenchies.g5_avance2_sc504.dto;

public class CreateUserRequest {
    private String usuario;
    private String password;
    private Long rolId;

    public CreateUserRequest() {}

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRolId() {
        return rolId;
    }
    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}
