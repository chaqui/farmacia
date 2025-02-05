package com.farmacia.models;

import java.util.Date;
import java.util.List;

import com.farmacia.dto.CompraDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column
    private Date fecha;
    
    @OneToMany(mappedBy = "compra")
    private List<DetalleCompra> detalleCompra;


    public Compra(CompraDto.POST compraDto, Proveedor proveedor) {
        this.proveedor = proveedor;

    }
}
