package com.farmacia.models;

import java.util.Date;
import java.util.List;

import com.farmacia.constants.EstadoSolicitud;
import com.farmacia.dto.SolicitudDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    Date fecha;
    @Column
    Long cantidad;
    @Column
    String estado;
    @Column
    String observacion;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    Sucursal sucursal;

    @OneToMany(mappedBy = "solicitud", orphanRemoval = true)
    List<DetalleSolicitud> detallesSolicitud;

    public Solicitud(SolicitudDto.POST solicitudDto, Sucursal sucursal) {
        this.fecha = solicitudDto.getFecha();
        this.cantidad = solicitudDto.getCantidad();
        this.estado = EstadoSolicitud.CREADA.getEstado();
        this.observacion = solicitudDto.getObservacion();
        this.sucursal = sucursal;
    }

}