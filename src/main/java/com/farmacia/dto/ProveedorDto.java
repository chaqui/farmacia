package com.farmacia.dto;

import com.farmacia.models.Proveedor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ProveedorDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class POST {
        public String nombre;
        public String direccion;
        public String telefono;
        public String email;
        public String contacto;

    }

    @Getter
    @Setter
    public static class PUT extends POST {

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GET extends POST {
        public Integer id;

        public GET(Proveedor proveedor) {
            this.id = proveedor.getId();
            this.nombre = proveedor.getNombre();
            this.direccion = proveedor.getDireccion();
            this.telefono = proveedor.getTelefono();
            this.email = proveedor.getEmail();
            this.contacto = proveedor.getContacto();
        }

    }

}
