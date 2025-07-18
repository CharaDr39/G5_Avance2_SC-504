package com.frenchies.g5_avance2_sc504.dto;

public class LoginRequest {
    private String usuario;
    private String password;

    public LoginRequest() {}

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
