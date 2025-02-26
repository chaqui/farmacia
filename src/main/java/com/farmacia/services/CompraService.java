package com.farmacia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmacia.dto.CompraDto;
import com.farmacia.dto.LoteDto;
import com.farmacia.exception.HttpException;
import com.farmacia.models.Compra;
import com.farmacia.models.DetalleCompra;
import com.farmacia.models.Lote;
import com.farmacia.models.Producto;
import com.farmacia.models.Proveedor;
import com.farmacia.repository.CompraRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private DetalleCompraService detalleCompraService;

    @Autowired
    private LoteService loteService;

    /**
     * Verifica si el proveedor y el producto existen, y crea una compra
     * 
     * @param compraDto datos de la compra
     * @throws HttpException si existe un error al crear la compra
     */
    @Transactional(rollbackOn = Exception.class)
    public void crearCompra(CompraDto.POST compraDto) throws HttpException {
        if (compraDto.getLotes().isEmpty()) {
            throw new HttpException("La compra debe tener al menos un lote", 400);
        }
        Proveedor proveedor = proveedorService.obtenerProveedor(compraDto.getIdProveedor());
        Compra compra = new Compra(compraDto, proveedor);
        compraRepository.save(compra);
        for (LoteDto.POST loteDto : compraDto.getLotes()) {
            Producto producto = productoService.obtenerProducto(loteDto.getIdProducto());
            Lote lote = loteService.agregarNuevoLote(loteDto, producto);
            detalleCompraService.crearDetalleCompra(compra, lote, loteDto.getCantidad());
        }

    }

    /**
     * Obtiene todas las compras
     * 
     * @return lista de compras
     */
    public List<Compra> obtenerCompras() {
        return compraRepository.findAll();
    }

    public List<DetalleCompra> obtenerDetalle(Long id) {
        return compraRepository.findById(id).get().getDetalleCompra();
    }

}
