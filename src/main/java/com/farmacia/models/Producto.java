package com.farmacia.models;

import java.util.Date;
import java.util.List;

import com.farmacia.dto.ProductoDto;

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
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private String categoria;

    @Column
    private String image;

    @Column
    private Date fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @OneToMany(mappedBy = "producto")
    private List<Lote> lotes;

    public Producto(ProductoDto.Post dto, Proveedor proveedor) {
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.categoria = dto.getCategoria();
        this.proveedor = proveedor;
    }

    public int getCantidad() {
        return this.lotes.stream().mapToInt(lote -> lote.getCantidad()).sum();
    }
}
