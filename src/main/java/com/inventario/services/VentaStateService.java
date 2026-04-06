package com.inventario.services;

import org.springframework.stereotype.Service;

import com.inventario.constants.EstadoVenta;
import com.inventario.dto.VentaDto;
import com.inventario.exception.HttpException;
import com.inventario.models.Cliente;
import com.inventario.models.Credito;
import com.inventario.models.Venta;
import com.inventario.repository.VentaRepository;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class VentaStateService extends VentaService {

    private final CreditoService creditoService;
    private final NotificacionService notificacionService;

    public VentaStateService(ClienteService clienteService,
            CreditoService creditoService, VentaRepository ventaRepository,
            SucursalLoteService sucursalLoteService, LoteService loteService,
            NotificacionService notificacionService) {
        super(ventaRepository, sucursalLoteService, loteService, clienteService, notificacionService);
        this.creditoService = creditoService;
        this.notificacionService = notificacionService;
    }

    /**
     * Cambiar venta a estado VERIFICADA
     * - Valida que la venta esté en estado CREADA
     * - Valida que tenga detalles
     * - Si esCredito=true: valida el límite disponible del cliente
     * - Si esCredito=false: autoriza automáticamente (venta al contado)
     * - NO desbloquea los productos (permanecen bloqueados)
     */
    @Transactional(rollbackOn = Exception.class)
    public Venta verificarVenta(Integer ventaId, VentaDto.Verificar dtoVerificar) throws HttpException {
        Venta venta = this.obtenerVentaPorId(ventaId);

        if (!venta.puedeSerVerificada()) {
            throw new HttpException("La venta debe estar en estado CREADA para ser verificada", 400);
        }

        boolean ventaTieneCredito = dtoVerificar.getEsCredito() || dtoVerificar.getMontoCredito() > 0f;
        // Si es crédito, validar límite de crédito del cliente
        if (ventaTieneCredito) {
            this.verificarCredito(dtoVerificar, venta);
            if (dtoVerificar.getMontoCredito() != null && dtoVerificar.getMontoCredito() > 0f) {
                venta.cambiarAVerificada(dtoVerificar.getMontoCredito());
            } else {
                venta.cambiarAVerificada(ventaTieneCredito);
            }

        }

        venta = this.ventaRepository.save(venta);

        // Notificar verificación de venta

        // Si NO es crédito (esCredito=false o montoCredito=0), autorizar
        // automáticamente
        if (!ventaTieneCredito) {

            venta = autorizarVenta(venta.getId());
        } else {
            this.notificacionService.notificarAutorizacionVenta(
                    venta.getId(),
                    venta.getCliente().getId(),
                    venta.getTotal().doubleValue(),
                    venta.getCliente().getNombre());
        }

        return venta;
    }

    /**
     * Validar que el cliente tenga límite de crédito disponible para la venta
     * - Si es crédito, el monto a considerar es el total de la venta
     * - Si no es crédito, el monto a considerar es 0 (no afecta el límite)
     * - Límite disponible = límite total del cliente - saldo actual de créditos
     * - Si el monto a crédito excede el límite disponible, se lanza una excepción
     * - Si el cliente no tiene límite definido, se asume que no tiene restricción
     * - Esta validación se ejecuta durante la verificación de la venta (antes de
     * autorizar)
     * 
     * @param dtoVerificar DTO con información de verificación (esCredito,
     *                     montoCredito)
     * @param venta        Venta que se está verificando
     * @throws HttpException si el cliente no tiene suficiente límite de crédito
     *                       disponible
     *                       Nota: esta validación solo se aplica si esCredito=true
     *                       o montoCredito > 0. Si es una venta al contado, no se
     *                       valida el crédito.
     */
    private void verificarCredito(VentaDto.Verificar dtoVerificar, Venta venta) throws HttpException {
        if (dtoVerificar.getEsCredito() == null || !dtoVerificar.getEsCredito()) {
            return; // No es crédito, no se requiere validación adicional
        }

        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            throw new HttpException("Venta sin cliente asociado, no se puede validar crédito", 400);
        }

        Float cantidadACredito = Boolean.TRUE.equals(dtoVerificar.getEsCredito()) ? venta.getTotal()
                : dtoVerificar.getMontoCredito();
        Float saldoCredito = cliente.getSaldoCredito();
        Float limiteDisponible = (cliente.getLimiteCredito() != null ? cliente.getLimiteCredito() : 0f)
                - saldoCredito;

        if (venta.getTotal() < cantidadACredito) {
            throw new HttpException(
                    "El monto a crédito no puede ser mayor al total de la venta. Total: " + venta.getTotal()
                            + ", Solicitado a crédito: " + cantidadACredito,
                    400);
        }

        if (cantidadACredito > limiteDisponible) {
            throw new HttpException(
                    "Crédito insuficiente. Disponible: " + limiteDisponible + ", Solicitado: " + cantidadACredito,
                    400);
        }

    }

    /**
     * Cambiar venta a estado AUTORIZADA
     * - Valida que esté en estado VERIFICADA
     * - Si es crédito (esCredito=true), crea el registro de crédito asociado
     * - El monto del crédito = montoCredito de la venta
     * - NO desbloquea/reduce productos (se hizo en estado CREADA)
     * 
     * @param ventaId ID de la venta a autorizar
     * @throws HttpException si la venta no cumple las condiciones para ser
     *                       autorizada
     */
    @Transactional(rollbackOn = Exception.class)
    public Venta autorizarVenta(Integer ventaId) throws HttpException {
        Venta venta = this.obtenerVentaPorId(ventaId);
        if (!venta.puedeSerAutorizada()) {
            throw new HttpException("La venta debe estar en estado VERIFICADA para ser autorizada", 400);
        }

        // Si es crédito, crear el registro de crédito
        if (venta.getEsCredito() != null && venta.getEsCredito()) {
            Cliente cliente = venta.getCliente();
            if (cliente == null) {
                throw new HttpException("Venta sin cliente asociado, no se puede autorizar crédito", 400);
            }

            Credito credito = new Credito(venta.getMontoCredito(), venta.getFecha(), cliente, venta);
            this.creditoService.guardarCredito(credito);
        }

        venta.cambiarAAutorizada();
        venta = this.ventaRepository.save(venta);

        return venta;
    }

    /**
     * Obtener estado actual de una venta
     */
    public EstadoVenta obtenerEstadoVenta(Integer ventaId) throws HttpException {
        Venta venta = obtenerVentaPorId(ventaId);
        return venta.getEstado();
    }

    /**
     * Cancelar una venta y liberar los productos
     * - Solo se puede cancelar si está en estado CREADA o VERIFICADA
     * - Al cancelar, se eliminan los DetalleVenta (liberando productos)
     * - Transición: CREADA/VERIFICADA → CANCELADA
     */
    @Transactional(rollbackOn = Exception.class)
    public Venta cancelarVenta(Integer ventaId) throws HttpException {
        Venta venta = this.obtenerVentaPorId(ventaId);

        if (!venta.puedeSercancelada()) {
            throw new HttpException(
                    "No se puede cancelar una venta en estado " + venta.getEstado()
                            + ". Solo se pueden cancelar ventas en estado CREADA o VERIFICADA",
                    400);
        }

        // Liberar los productos eliminando los detalles de venta
        // Al eliminar los detalles, la cantidad se recalcula automáticamente en el Lote
        venta.getDetalleVentas().clear();

        venta.cambiarACancelada();
        return this.ventaRepository.save(venta);
    }

    /**
     * Obtener todas las ventas de un estado específico
     */
    public List<Venta> obtenerVentasPorEstado(EstadoVenta estado) {
        return this.ventaRepository.findByEstado(estado);
    }

    /**
     * Obtener todas las ventas agrupadas por estado
     */
    public List<Venta> obtenerTodasLasVentas() {
        return this.ventaRepository.findAll();
    }

}
