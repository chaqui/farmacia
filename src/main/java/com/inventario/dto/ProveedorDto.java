package com.inventario.dto;

import java.util.List;
import com.inventario.models.Proveedor;

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
        public List<Integer> marcaIds;
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
        public Long id;

        public GET(Proveedor proveedor) {
            this.id = proveedor.getId();
            this.nombre = proveedor.getNombre();
            this.direccion = proveedor.getDireccion();
            this.telefono = proveedor.getTelefono();
            this.email = proveedor.getEmail();
            this.contacto = proveedor.getContacto();
            this.marcaIds = proveedor.getMarcas().stream()
                    .map(marca -> marca.getId())
                    .toList();
        }

    }

}
