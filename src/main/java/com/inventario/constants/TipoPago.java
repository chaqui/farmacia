package com.inventario.constants;

public enum TipoPago {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    DEPOSITO("Depósito");

    private final String descripcion;

    TipoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
