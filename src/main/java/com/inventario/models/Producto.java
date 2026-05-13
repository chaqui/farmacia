package com.inventario.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.inventario.dto.ProductoDto;
import com.inventario.models.Categoria;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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

    @Column(unique = true)
    private String codigo;

    @Column
    private Float porcentajeDescuento;

    @Column
    private Float porcentajeGanancia;

    @Column
    private String estanteria;

    @Column
    private Integer nivel;

    @Column
    private String bodega;

    @ManyToMany
    @JoinTable(
        name = "producto_categoria",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias = new ArrayList<>();



    @Column 
    private Integer cantidadMinima;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = true)
    private Marca marca;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "producto")
    private List<Lote> lotes = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fotografia> fotografias = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "producto_relacionados",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "relacionado_id")
    )
    private List<Producto> relacionados = new ArrayList<>();

    public Producto(ProductoDto.Post dto, Proveedor proveedor) {
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.proveedor = proveedor;
        this.codigo = dto.getCodigo();
        this.porcentajeDescuento = dto.getPorcentajeDescuento();
        this.porcentajeGanancia = dto.getPorcentajeGanancia();
        if (dto != null) {
            this.estanteria = dto.getEstanteria();
            this.nivel = dto.getNivel();
            this.bodega = dto.getBodega();
        }
    }

    public Long getCantidad() {
        return this.lotes.stream().mapToLong(lote -> lote.getCantidad()).sum();
    }

    public void agregarRelacionado(Producto producto) {
        this.relacionados.add(producto);
    }

    public void agregarFotografia(Fotografia fotografia) {
        this.fotografias.add(fotografia);
    }

    public void agregarCategoria(Categoria categoria) {
        this.categorias.add(categoria);
    }
}
