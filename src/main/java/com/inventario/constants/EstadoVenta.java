package com.inventario.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoVenta {

    CREADA("1", "CRE", "Creada"),
    VERIFICADA("2", "VER", "Verificada"),
    AUTORIZADA("3", "AUT", "Autorizada"),
    CANCELADA("4", "CAN", "Cancelada");

    private final String codigo;
    private final String abreviatura;
    private final String descripcion;

  
    public String getEstado() {
        return codigo;
    }

    public static EstadoVenta fromCodigo(String codigo) {
        for (EstadoVenta estado : EstadoVenta.values()) {
            if (estado.codigo.equals(codigo)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de venta no válido: " + codigo);
    }
}
