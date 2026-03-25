package com.inventario.dto;

import java.util.Date;
import java.util.List;

import com.inventario.models.Solicitud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SolicitudDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class POST {
        protected Long idSucursal;
        protected String observacion;
        protected Date fecha;
        private List<SolicitudDetalleDto.Post> detalles;
    }

    @Getter
    @NoArgsConstructor
    public static class GET extends POST {
        private Long id;
        private String sucursal;
        private String estado;

        public GET(Solicitud solicitud) {
            this.id = solicitud.getId();
            this.fecha = solicitud.getFecha();
            this.estado = solicitud.getEstado();
            this.sucursal = solicitud.getSucursal().getNombre();
            this.idSucursal = solicitud.getSucursal().getId();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ENVIAR {
        private List<LoteDto.ENVIAR> lotes;

    }

}
