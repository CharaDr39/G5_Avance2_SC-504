
package com.frenchies.g5_avance2_sc504.dto;


public class CreateRoleRequest {
    private String nombre;
    private String descripcion;  // opcional

    public CreateRoleRequest() { }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
