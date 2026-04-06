package com.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificarVentaDto {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Integer ventaId;

    @NotNull(message = "esCredito es obligatorio")
    private Boolean esCredito;

}
