package com.inventario.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.inventario.dto.VentaDetalleDto;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.DetalleVenta;
import com.inventario.models.Lote;
import com.inventario.models.Sucursal;
import com.inventario.models.SucursalLote;
import com.inventario.models.Venta;
import com.inventario.models.Cliente;
import com.inventario.repository.VentaRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class VentaService {

    protected final VentaRepository ventaRepository;

    private final SucursalLoteService sucursalLoteService;

    private final ClienteService clienteService;

    private final LoteService loteService;

    private final NotificacionService notificacionService;

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext applicationContext;

    public VentaService(VentaRepository ventaRepository,
            SucursalLoteService sucursalLoteService, LoteService loteService, ClienteService clienteService,
            NotificacionService notificacionService) {
        this.ventaRepository = ventaRepository;
        this.sucursalLoteService = sucursalLoteService;
        this.loteService = loteService;
        this.clienteService = clienteService;
        this.notificacionService = notificacionService;
    }

    @Transactional(rollbackOn = Exception.class)
    public Venta crearVenta(VentaDto.Post ventaDto) throws HttpException {
        Cliente cliente = null;
        if(ventaDto.getClienteId() != null) {
             cliente = this.clienteService.obtenerClientePorId(ventaDto.getClienteId());
        }

        Venta venta = new Venta(ventaDto, cliente);
        log.info("Creando venta para cliente: " + (cliente != null ? cliente.getNombre() : ventaDto.getNombreCliente()) + ", Total: " + venta.getTotal());
        this.ventaRepository.save(venta);
        this.agregarSubdetallesSinSucursal(venta, ventaDto.getDetalles());

       

        // Si la variable de entorno indica venta directa, enviar a verificación (y autorizar si aplica)
        boolean ventaDirecta = Boolean.parseBoolean(env.getProperty("VENTA_DIRECTA", env.getProperty("venta.directa.enabled", "false")));
        if (ventaDirecta) {
            VentaDto.Verificar dto = new VentaDto.Verificar();
            dto.setEsCredito(false);
            // Obtener el servicio de estado y ejecutar la verificación (esto autoriza automáticamente si no es crédito)
            VentaStateService ventaStateService = this.applicationContext.getBean(VentaStateService.class);
            ventaStateService.verificarVenta(venta.getId(), dto);
        }
        else {
            // Notificar creación de venta
            notificacionService.notificarVerificacionVenta(
                venta.getId(),
                cliente != null ? cliente.getId() : null,
                venta.getTotal().doubleValue(),
                cliente != null ? cliente.getNombre() : ventaDto.getNombreCliente()
            );
        }

        return venta;
    }

    private void agregarSubdetallesSinSucursal(Venta venta, List<VentaDetalleDto.Post> detalles) throws HttpException {
        for (VentaDetalleDto.Post detalleDto : detalles) {
            Lote lote = this.loteService.reduceCantidadLote(detalleDto.getLote(), detalleDto.getCantidad());
            DetalleVenta detalleVenta = new DetalleVenta(detalleDto, lote);
            venta.addDetalle(detalleVenta);
        }
        this.ventaRepository.save(venta);
    }

    public List<VentaDto.GetSinSucursal> obtenerVentas() {
        List<Venta> ventas = this.ventaRepository.findAll();
        return ventas.stream().map(VentaDto.GetSinSucursal::new).toList();
    }

    public List<VentaDto.GetSinSucursal> obtenerVentas(LocalDate desde, LocalDate hasta) {
        return this.ventaRepository.findByFechaBetweenOrderByFechaDescIdDesc(desde, hasta)
                .stream()
                .map(VentaDto.GetSinSucursal::new)
                .toList();
    }

    public List<VentaDto.GetConSucursal> obtenerVentas(Long sucursalId, LocalDate desde, LocalDate hasta) {
        return this.ventaRepository
                .findBySucursalIdAndFechaBetweenOrderByFechaDescIdDesc(sucursalId, desde, hasta)
                .stream()
                .map(VentaDto.GetConSucursal::new)
                .toList();
    }

    @Transactional(rollbackOn = Exception.class)
    public Venta crearVenta(VentaDto.Post ventaDto, Sucursal sucursal) throws HttpException {
        Cliente cliente = this.clienteService.obtenerClientePorId(ventaDto.getClienteId());
        Venta venta = new Venta(ventaDto, sucursal, cliente);
        this.ventaRepository.save(venta);
        this.agregarSubdetallesConSucursal(venta, ventaDto.getDetalles());

  

        // Si la variable de entorno indica venta directa, enviar a verificación (y autorizar si aplica)
        boolean ventaDirecta = Boolean.parseBoolean(env.getProperty("VENTA_DIRECTA", env.getProperty("venta.directa.enabled", "false")));
        if (ventaDirecta) {
            VentaDto.Verificar dto = new VentaDto.Verificar();
            dto.setEsCredito(false);
            VentaStateService ventaStateService = this.applicationContext.getBean(VentaStateService.class);
            ventaStateService.verificarVenta(venta.getId(), dto);
        }
        else {
            // Notificar creación de venta
            notificacionService.notificarVerificacionVenta(
                venta.getId(),
                cliente != null ? cliente.getId() : null,
                venta.getTotal().doubleValue(),
                cliente != null ? cliente.getNombre() : ventaDto.getNombreCliente()
            );
        }

        return venta;
    }

    private void agregarSubdetallesConSucursal(Venta venta, List<VentaDetalleDto.Post> detalles) throws HttpException {
        for (VentaDetalleDto.Post detalleDto : detalles) {
            SucursalLote lote = this.sucursalLoteService.reducirCantidadLoteEnSucursal(venta.getSucursal().getId(),
                    detalleDto.getLote(), detalleDto.getCantidad());
            DetalleVenta detalleVenta = new DetalleVenta(detalleDto, lote.getLote());
            venta.addDetalle(detalleVenta);

        }
        this.ventaRepository.save(venta);

    }

    protected Venta obtenerVentaPorId(Integer ventaId) throws HttpException {
        return this.ventaRepository.findById(ventaId)
                .orElseThrow(() -> new HttpException("Venta no encontrada", 404));
    }

    public List<VentaDetalleDto.Get> obtenerDetallesVenta(Integer ventaId) throws HttpException {
        Venta venta = this.obtenerVentaPorId(ventaId);
        if (venta.getDetalleVentas() == null || venta.getDetalleVentas().isEmpty()) {
            throw new HttpException("La venta no tiene detalles", 404);
        }
        return venta.getDetalleVentas().stream()
                .map(VentaDetalleDto.Get::new)
                .toList();
    }

}
